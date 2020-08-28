package com.eric;

import com.eric.starter.service.CalculateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @Author: Eric Liang
 * @Since: 2020-8-27 20:36
 */
@RestController
public class Demo2Controller {
    @Autowired
    private CalculateService calculateService;

    @RequestMapping("/add")
    public double add(double v1, double v2) {
        return calculateService.add(v1, v2);
    }

    @RequestMapping("/sub")
    public double sub(double v1,double v2){
        return calculateService.sub(v1, v2);
    }

    @RequestMapping("/mul")
    public double mul(double v1,double v2){
        return calculateService.mul(v1, v2);
    }

    @RequestMapping("/setScale")
    public double setScale(double v,int scale){
        return calculateService.setScale(v,scale);
    }
    @RequestMapping("/setScale2")
    public double setScale(double v){
        return calculateService.setScale(v);
    }
}
