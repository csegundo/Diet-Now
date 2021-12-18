package com.dietnow.app.ucm.fdi.DietNow;

import java.util.HashMap;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.google.firebase.auth.UserRecord.UpdateRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dietnow/api/user")
public class ApiUserController {
    
    // Este metodo es solo de prueba
    @GetMapping("/{uid}")
    @ResponseBody
    public String getUserInfoById(@PathVariable String uid){
        return "User ID: " + uid;
    }

    @PostMapping("/create")
    public String createFirebaseuser(@RequestBody HashMap<String, String> params){
        try {
            Boolean checked = DietNowTokens.checkTokens(params.get("sender"), params.get("code"));

            if(!checked){
                return GenerateResponse.generateJSON(false, "Message:Fallo al autenticar la petición");
            }

            CreateRequest request = new CreateRequest()
                .setEmail(params.get("email"))
                .setPassword(params.get("password"));
        
            UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
            return GenerateResponse.generateJSON(true, userRecord.getUid());
        } catch (Exception e) {
            e.printStackTrace();
            return GenerateResponse.generateJSON(false, "Message:Se ha producido un error");
        }
    }

    @PostMapping("/edit/email")
    public String editUserEmail(@RequestBody HashMap<String, String> params){
        try {
            Boolean checked = DietNowTokens.checkTokens(params.get("sender"), params.get("code"));

            if(!checked){
                return GenerateResponse.generateJSON(false, "Message:Fallo al autenticar la petición");
            }

            UpdateRequest request = new UpdateRequest(params.get("uid"))
                .setEmail(params.get("email"));

            UserRecord userRecord = FirebaseAuth.getInstance().updateUser(request);

            return GenerateResponse.generateJSON(true, userRecord.getUid());
        } catch (Exception e) {
            e.printStackTrace();
            return GenerateResponse.generateJSON(false, "Message:Se ha producido un error");
        }
    }

    @PostMapping("/edit/password")
    public String editUserPassword(@RequestBody HashMap<String, String> params){
        try {
            Boolean checked = DietNowTokens.checkTokens(params.get("sender"), params.get("code"));

            if(!checked){
                return GenerateResponse.generateJSON(false, "Message:Fallo al autenticar la petición");
            }

            UpdateRequest request = new UpdateRequest(params.get("uid"))
                .setPassword(params.get("password"));
                
            UserRecord userRecord = FirebaseAuth.getInstance().updateUser(request);

            return GenerateResponse.generateJSON(true, userRecord.getUid());
        } catch (Exception e) {
            e.printStackTrace();
            return GenerateResponse.generateJSON(false, "Message:Se ha producido un error");
        }
    }
}