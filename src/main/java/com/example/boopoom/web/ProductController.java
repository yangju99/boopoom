package com.example.boopoom.web;

import com.example.boopoom.domain.product.Gpu;
import com.example.boopoom.domain.product.Product;
import com.example.boopoom.domain.product.Ram;
import com.example.boopoom.domain.product.Ssd;
import com.example.boopoom.service.ProductService;
import com.example.boopoom.web.forms.product.GpuForm;
import com.example.boopoom.web.forms.product.ProductForm;
import com.example.boopoom.web.forms.product.RamForm;
import com.example.boopoom.web.forms.product.SsdForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    //먼저 새로운 상품을 등록하려고 하면 gpu, ssd, ram 중 항목을 결정함
    @GetMapping("/products/new")
    public String slectForm(Model model){
        return "products/slectCategory";
    }
    // 항목이 결정되면 해당 항목의 입력 form
    @GetMapping("/products/new/{productCategory}")
    public String createForm(@PathVariable("productCategory") String productCategory, Model model){
        if (productCategory.equals("G")){
            model.addAttribute("form", new GpuForm());
            return "products/createGpuForm";
        } else if (productCategory.equals("S")){
            model.addAttribute("form", new SsdForm());
            return "products/createSsdForm";
        } else{
            model.addAttribute("form", new RamForm());
            return "products/createRamForm";
        }
    }

    @PostMapping("/products/new/{productCategory}")
    public String create(@PathVariable("productCategory") String productCategory,
                         @ModelAttribute("form") ProductForm productForm){
        if (productCategory.equals("G")){
            Gpu gpu = Gpu.createGpu((GpuForm) productForm);
            productService.saveProduct(gpu);
        } else if (productCategory.equals("S")){
            Ssd ssd = Ssd.createSsd((SsdForm) productForm);
            productService.saveProduct(ssd);
        } else{
            Ram ram = Ram.createRam((RamForm) productForm);
            productService.saveProduct(ram);
        }
        return "redirect:/products";
    }

    @GetMapping("/products")
    public String productList(Model model){
        List<Product> products = productService.findProducts();
        model.addAttribute("products", products);
        return "products/productList";
    }

    @GetMapping("/products/{productId}/edit")
    public String updateProductForm(@PathVariable("productId") Long productId, Model model){
        Product product = productService.findOne(productId);

        if (product instanceof Gpu){
            model.addAttribute("form", GpuForm.fromEntity((Gpu) product));
        } else if (product instanceof Ram){
            model.addAttribute("form", RamForm.fromEntity((Ram) product));
        } else {
            model.addAttribute("form", SsdForm.fromEntity((Ssd) product));
        }

        model.addAttribute("productId", productId);
        return "products/updateProductForm";
    }

    @PostMapping("/products/{productId}/edit")
    public String updateProduct(@PathVariable("productId") Long productId, @ModelAttribute("form") ProductForm form){
        productService.updateProduct(productId, form.getModelName(), form.getModelNumber(), form.getReleaseYear(), form.getBrand(), form.getGeneration());
        return "redirect:/products";
    }
}
