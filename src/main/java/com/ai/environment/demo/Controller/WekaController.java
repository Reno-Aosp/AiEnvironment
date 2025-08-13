package com.ai.environment.demo.Controller;

import com.ai.environment.demo.Service.WekaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/weka")
public class WekaController {

    @Autowired
    private WekaService wekaService;

    @GetMapping("/tree2") // Change this line
    public String showTree() throws Exception {
        return wekaService.trainAndShowTree();
    }

    // Optional: Predict using the saved model and small data
    @GetMapping("/predict")
    public String predict() throws Exception {
        var data = wekaService.loadSmallData();
        var model = wekaService.loadModel();
        double label = model.classifyInstance(data.instance(0));
        return "Predicted label for first instance: " + label;
    }
}
