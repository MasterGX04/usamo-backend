package com.competitionmathacademy.mathapi.service;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;

import org.springframework.stereotype.Service;

import com.competitionmathacademy.mathapi.config.PayPalConfig;
import com.competitionmathacademy.mathapi.model.Product;
import com.competitionmathacademy.mathapi.repository.ProductRepository;

import java.util.Optional;

@Service
public class ProductService {
    private final PayPalConfig payPalConfig;
    private final ProductRepository productRepository;

    public ProductService(PayPalConfig payPalConfig, ProductRepository productRepository) {
        this.payPalConfig = payPalConfig;
        this.productRepository = productRepository;
    }

    public String createOrRetrieveProduct() {
        String productName = "Monthly Subscription Product";

        Optional<Product> existingProduct = productRepository.findByName(productName);
        if (existingProduct.isPresent()) {
            System.out.println("Product found in database. Returning productId: " + existingProduct.get().getProductId());
            return existingProduct.get().getProductId();
        }
        //System.out.println("Product not found in database. Proceeding to create via PayPal API.");


        try {
            String accessToken = payPalConfig.generateAccessToken();
            //System.out.println("Generated Access Token: " + accessToken);

            URL url = new URI(payPalConfig.getBaseUrl() + "/v1/catalogs/products").toURL();
            //System.out.println("PayPal API URL: " + url);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            String payload = "{\n" +
                "  \"name\": \"Monthly Subscription Product\",\n" +
                "  \"description\": \"Subscription product for recurring payments.\",\n" +
                "  \"type\": \"SERVICE\",\n" +
                "  \"category\": \"SOFTWARE\",\n" +
                "  \"image_url\": \"https://ih1.redbubble.net/image.4935429074.1625/bg,f8f8f8-flat,750x,075,f-pad,750x1000,f8f8f8.jpg\",\n" +
                "  \"home_url\": \"https://usaco.guide\"\n" +
                "}";

                //System.out.println("Payload being sent to PayPal API: " + payload);

            try (OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream())) {
                writer.write(payload);
                writer.flush();
            } 

           //System.out.println("Response Code from PayPal API: " + conn.getResponseCode());
            
            if (conn.getResponseCode() == 201) {
                try (Scanner scanner = new Scanner(conn.getInputStream())) {
                    scanner.useDelimiter("\\A");
                    String response = scanner.hasNext() ? scanner.next() : "";
                    
                    // Extract product ID from the response
                    String productId = response.split("\"id\":\"")[1].split("\"")[0];

                    // Save the product details in the database
                    Product product = new Product();
                    product.setProductId(productId);
                    product.setName(productName);
                    product.setDescription("Subscription product for recurring payments.");
                    product.setType("SERVICE");
                    product.setCategory("SOFTWARE");
                    product.setImageUrl("https://ih1.redbubble.net/image.4935429074.1625/bg,f8f8f8-flat,750x,075,f-pad,750x1000,f8f8f8.jpg");
                    product.setHomeUrl("https://usaco.guide");

                    productRepository.save(product);

                    return productId;
                }
            } else {
                try (Scanner scanner = new Scanner(conn.getErrorStream())) {
                    scanner.useDelimiter("\\A");
                    String errorResponse = scanner.hasNext() ? scanner.next() : "";
                    throw new RuntimeException("PayPal API Error: " + errorResponse);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create product: " + e.getMessage());
        }
    } 
}
