package com.ai.environment.demo.Controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import com.ai.environment.demo.ReqDTO.PredictReq;
import com.ai.environment.demo.Service.ServiceAI;
import com.ai.environment.demo.Service.WekaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;



@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Slf4j
public class AIController {
  private final ServiceAI svc; // No need for constructor anymore!
  private final WekaService wekaDemoService;

  @PostMapping("/predict")
  public ResponseEntity<?> predict(@RequestBody PredictReq request) {
      log.debug("Received prediction request: {}", request);
      return svc.predict(request.getText());
  }

  @GetMapping("/health")
  public Map<String, String> health() {
      return Map.of("status", "ok");
  }

  @GetMapping("/model-info")
  public Map<String, String> modelInfo() {
      // You can expand this with real info if available
      return Map.of(
          "model", "ONNX",
          "tokenizer", "HuggingFace",
          "status", "loaded"
      );
  }

  @PostMapping("/reload-model")
  public ResponseEntity<?> reloadModel() {
      try {
          svc.reloadModel();
          return ResponseEntity.ok(Map.of("status", "reloaded"));
      } catch (Exception e) {
          return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
      }
  }

  @PostMapping("/train")
  public ResponseEntity<?> trainModel(@RequestParam("dataPath") String dataPath) {
      try {
          // Load training data
          DataSource source = new DataSource(dataPath);
          Instances data = source.getDataSet();
          // Set class index to the last attribute
          if (data.classIndex() == -1)
              data.setClassIndex(data.numAttributes() - 1);

          // Build and train the classifier
          J48 tree = new J48();
          tree.buildClassifier(data);

          // Return the model as a response (or save it, depending on your use case)
          return ResponseEntity.ok(Map.of("model", tree.toString()));
      } catch (Exception e) {
          return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
      }
  }

  @GetMapping("/weka/tree-ai")
  public ResponseEntity<?> getWekaTree() {
      try {
          String tree = wekaDemoService.trainAndShowTree();
          return ResponseEntity.ok(tree);
      } catch (Exception e) {
          return ResponseEntity.status(500).body("Weka error: " + e.getMessage());
      }
  }

  @GetMapping("/model-status")
  public Map<String, Object> modelStatus() {
      boolean loaded = svc.isModelLoaded();
      return Map.of(
          "modelLoaded", loaded,
          "message", loaded ? "Model is loaded and ready." : "Model is missing. Please upload model.onnx and tokenizer.json."
      );
  }

  // ...add other endpoints here...
}
