package com.dietnow.app.ucm.fdi.DietNow;

import java.util.HashMap;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/dietnow/api/user")
public class ApiUserController {
    
    // Este metodo es solo de prueba
    @GetMapping("/{uid}")
    @ResponseBody
    public String getUserInfoById(@PathVariable String uid){
        return "User ID: " + uid;
    }

    @PostMapping(value={"/create","/create/"})
    public String createFirebaseuser(@RequestBody HashMap<String, String> params){
        try {
            // params = params.substring(1, params.length() - 2);
            // String[] _params = params.split(",");

            // CreateRequest request = new CreateRequest()
            //     .setEmail(params.get("email"))
            //     .setPassword(params.get("password"));
        
            // UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }
}