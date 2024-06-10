package com.louis.flashsale.controller;

import com.louis.flashsale.exception.InsufficientInventoryException;
import com.louis.flashsale.exception.OrderInvalidationException;
import com.louis.flashsale.exception.RepeatSeckillException;
import com.louis.flashsale.exception.UnpaidException;
import com.louis.flashsale.persistence.entity.SeckillItem;
import com.louis.flashsale.service.SeckillSevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/seckill")
public class SeckillController {

    @Autowired
    private SeckillSevice seckillSevice;

    @GetMapping("/list")
    public String getAllItem (Model model) {
        List<SeckillItem> items = seckillSevice.getAllItem();
        translateItemImgUrl(items);
        model.addAttribute("items" , items);
        return "list";
    }

    @RequestMapping("/{id}")
    public String getItem (Model model , @PathVariable Integer id , String mobile) {
        if (id == null) {
            return "list";
        }
        try {
            SeckillItem item = seckillSevice.getItemById(id , mobile);
            List<SeckillItem> items = new ArrayList<>();
            items.add(item);
            translateItemImgUrl(items);
            model.addAttribute("item" , item);
            model.addAttribute("mobile" , mobile);
        }
        catch (RepeatSeckillException e) {
            model.addAttribute("error" , "您已經限時搶購過該商品！");
        }
        catch (UnpaidException e) {
            model.addAttribute("id" , id);
            model.addAttribute("mobile" , mobile);
            model.addAttribute("msg" , "您已經限時搶購過該商品，但還未支付！");
            return "pay";
        }
        catch (OrderInvalidationException e) {
            model.addAttribute("error" , "由於您未支付產品，訂單已經故障");
        }
        return "item";
    }

    @PostMapping("/exec")
    public String execSeckill (Model model , Integer id , String mobile) {
        try {
            seckillSevice.execSeckill(id , mobile);
            model.addAttribute("id" , id);
            model.addAttribute("mobile" , mobile);
            model.addAttribute("msg" , "限時搶購成功，請在10分鐘內支付");

        }
        catch (InsufficientInventoryException e) {
            model.addAttribute("error" , "商品已經售完！");
        }
        return "pay";
    }

    // 從回傳訊息 改成 可跳回主頁
    /*
    @GetMapping("/pay")
    @ResponseBody
    public String pay (Integer id , String mobile) {
        try {
            seckillSevice.pay(id , mobile);
            return "支付成功";
        }
        catch (OrderInvalidationException e) {
            return "您的訂單已經故障";
        }
    }
     */
    @GetMapping("/pay")
    public String pay(Integer id, String mobile, Model model) {
        try {
            seckillSevice.pay(id, mobile);
            model.addAttribute("msg", "支付成功");
        } catch (OrderInvalidationException e) {
            model.addAttribute("error", "您的訂單已經故障");
        }
        return "result";
    }

    /**
     * 對商品圖片的 URL 進行轉換
     *
     * @param items 商品列表
     */
    private void translateItemImgUrl (List<SeckillItem> items) {
        for (SeckillItem item : items) {
            item.setImgUrl(getServerInfo() + "/img/" + item.getImgUrl());
        }
    }

    /**
     * 得到後端程式的上下文路徑
     *
     * @return 上下文路徑
     */
    private String getServerInfo () {
        ServletRequestAttributes attrs = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();

        StringBuffer sb = new StringBuffer();
        HttpServletRequest request = attrs.getRequest();
        sb.append(request.getContextPath());
        return sb.toString();
    }
}
