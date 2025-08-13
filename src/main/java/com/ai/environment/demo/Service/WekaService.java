package com.ai.environment.demo.Service;

import org.springframework.stereotype.Service;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.SerializationHelper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@Service
public class WekaService {

    // Train a tree on BigData and return the tree as a string
    public String trainAndShowTree() throws Exception {
        System.out.println("Trying to load: BigData/Weka.arff");
        DataSource source = new DataSource("BigData/Weka.arff");
        Instances data = source.getDataSet();
        if (data == null) {
            throw new IOException("Failed to load BigData/Weka.arff!");
        }
        if (data.classIndex() == -1)
            data.setClassIndex(data.numAttributes() - 1);

        J48 tree = new J48();
        tree.buildClassifier(data);

        return tree.toString();
    }

    // Load small data from resources
    public Instances loadSmallData() throws Exception {
        System.out.println("Trying to load: /SmollData/Weka.arff from resources");
        InputStream dataIs = getClass().getResourceAsStream("/SmollData/Weka.arff");
        if (dataIs == null) {
            throw new FileNotFoundException("Could not find /SmollData/Weka.arff in resources!");
        }
        DataSource source = new DataSource(dataIs);
        Instances data = source.getDataSet();
        if (data == null) {
            throw new IOException("Failed to load Instances from ARFF file!");
        }
        data.setClassIndex(data.numAttributes() - 1);
        return data;
    }

    // Load trained model from resources
    public Classifier loadModel() throws Exception {
        InputStream modelIs = getClass().getResourceAsStream("/SmollData/WekaTrained.model");
        return (Classifier) SerializationHelper.read(modelIs);
    }
}