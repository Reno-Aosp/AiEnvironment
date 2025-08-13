package com.ai.environment.demo.Service;

import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Map;

@Service
@Slf4j // Adds logger automatically
public class ServiceAI implements AutoCloseable {
    private final OrtEnvironment env = OrtEnvironment.getEnvironment();
    private OrtSession session;
    private HuggingFaceTokenizer tok;

    public ServiceAI() {
        try {
            File modelFile = new File("weights/model.onnx");
            File tokenizerFile = new File("weights/tokenizer.json");

            if (!modelFile.exists() || !tokenizerFile.exists()) {
                log.warn("Model or tokenizer file not found in weights/ folder. AI endpoints will be unavailable until files are provided.");
                session = null;
                tok = null;
                return;
            }

            this.session = env.createSession(modelFile.getPath(), new OrtSession.SessionOptions());
            this.tok = HuggingFaceTokenizer.newInstance(tokenizerFile.getPath());
            log.info("AI model loaded successfully");
        } catch (Exception e) {
            log.error("Error initializing ServiceAI: {}", e.getMessage());
            session = null;
            tok = null;
        }
    }

    public ResponseEntity<?> predict(String text) {
        if (session == null || tok == null) {
            log.warn("Prediction requested but model is not loaded.");
            return ResponseEntity.status(503).body(Map.of("error", "AI model not loaded. Please upload model.onnx and tokenizer.json to weights/ folder."));
        }
        log.info("Predicting for text: {}", text);
        try {
            var enc = tok.encode(new String[]{text});
            long[] ids = enc.getIds();
            long[] mask = enc.getAttentionMask();

            // ONNX expects shape: [batch_size, sequence_length]
            long[][] ids2d = new long[1][ids.length];
            long[][] mask2d = new long[1][mask.length];
            ids2d[0] = ids;
            mask2d[0] = mask;

            try (OnnxTensor tIds  = OnnxTensor.createTensor(env, ids2d);
                 OnnxTensor tMask = OnnxTensor.createTensor(env, mask2d)) {

                var out = session.run(Map.of("input_ids", tIds, "attention_mask", tMask));
                float[][] logits = (float[][]) out.get(0).getValue();
                double pPos = softmax2(logits[0][0], logits[0][1]);
                
                log.debug("Prediction completed successfully");
                return ResponseEntity.ok(Map.of("positive", pPos, "negative", 1.0 - pPos));
            }
        } catch (Exception e) {
            log.error("Error during prediction: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    private static double softmax2(float a0, float a1) {
        double m = Math.max(a0, a1), e0 = Math.exp(a0 - m), e1 = Math.exp(a1 - m);
        return e1 / (e0 + e1);
    }

    public void reloadModel() throws Exception {
        log.info("Reloading AI model...");
        
        // Close existing resources
        if (session != null) {
            session.close();
        }
        if (tok != null) {
            tok.close();
        }
        
        // Reload model files
        File modelFile = new File("weights/model.onnx");
        File tokenizerFile = new File("weights/tokenizer.json");

        if (!modelFile.exists() || !tokenizerFile.exists()) {
            log.error("Model or tokenizer file not found during reload");
            throw new IllegalStateException("Model or tokenizer file not found in weights/ folder.");
        }

        this.session = env.createSession(modelFile.getPath(), new OrtSession.SessionOptions());
        this.tok = HuggingFaceTokenizer.newInstance(tokenizerFile.getPath());
        
        log.info("AI model reloaded successfully");
    }

    public boolean isModelLoaded() {
        return session != null && tok != null;
    }

    @Override 
    public void close() throws Exception { 
        if (session != null) {
            session.close(); 
        }
        if (tok != null) {
            tok.close();
        }
        log.info("ServiceAI closed");
    }
}
