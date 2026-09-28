package com.rana.controller;


import com.rana.model.Product;
import com.rana.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("products")
    public ResponseEntity<List<Product>> getProducts(){
        return new ResponseEntity<>(productService.getAllProducts(),HttpStatus.ACCEPTED);
    }

    @GetMapping("product/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable("id") int id){
        Product product=productService.getProductById(id);
        if(product!=null){
            return new ResponseEntity<>(product,HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/product")
    public ResponseEntity<?> addProduct(@RequestPart Product product, @RequestPart MultipartFile imageFile){
       try {
           Product savedProduct = productService.addProduct(product, imageFile);
           return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
       }catch(IOException e){
           return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }

    @GetMapping("/product/{productId}/image")
    public ResponseEntity<byte[]> getImageByProductId(@PathVariable("productId") int productId){
        Product product=productService.getProductById(productId);
        if (product == null || product.getImageData() == null || product.getImageType() == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(product.getImageType())).body(product.getImageData());
    }

    @PutMapping("/product/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable("id") Integer id,@RequestPart Product product,@RequestPart MultipartFile imageFile){
        Product updatedProduct=null;
        try{
            updatedProduct=productService.updateProduct(product,imageFile);
            return ResponseEntity.ok().body("Updated");
        }catch(IOException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable("id") int id){
        Product product=productService.getProductById(id);
        if(product!=null){
            productService.deleteProduct(id);
            return ResponseEntity.ok().body("deleted");
        }
        else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/products/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String keyword){
        List<Product> products=productService.searchproducts(keyword);
        System.out.println("searching with: "+keyword);
        return ResponseEntity.ok().body(products);
    }
}
