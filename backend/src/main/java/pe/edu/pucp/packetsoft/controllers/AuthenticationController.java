package pe.edu.pucp.packetsoft.controllers;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.naming.AuthenticationException;

import pe.edu.pucp.packetsoft.models.PersonasModel.Usuario;
import pe.edu.pucp.packetsoft.services.EmailService;
import pe.edu.pucp.packetsoft.services.PersonasService.UsuarioService;
import pe.edu.pucp.packetsoft.utils.JWTUtil;
import pe.edu.pucp.packetsoft.utils.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthenticationController {

    @Value("${keyCode}")
    private String keyCode;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private UsuarioService userService;

    @Autowired
    private EmailService emailService;

    //Login de back luego de pasar por la autenticacion de google en front
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PostMapping(value = "/googlepostlogin")
    Map<String, Object> googlePostlogin(@RequestHeader(value = "Authorization") String tokenOauth2,
                                        @RequestBody Usuario dto) throws AuthenticationException, SQLException{
        //Validar clave secreta entre el cliente y servidor
        if(tokenOauth2 != keyCode)
            throw new AuthenticationException();

        //Validar duplicado con usuario de algun otro proveedor
        /*if(userService.duplicadoExterno(dto.getUsuario(), 1))
            throw new SQLException();*/
    
        Usuario usuarioLogueado = userService.getGoogleUserByUsername(dto.getUsuario());

        if(usuarioLogueado == null){
            //Si no existe, entonces pertenece a google
            dto.setProveedor(1);
            usuarioLogueado = userService.register(dto);
        }
        
        Map<String, Object> result = new HashMap<>();
        String token = jwtUtil.create(String.valueOf(usuarioLogueado.getId()), usuarioLogueado.getUsuario());

        //Retornar objeto con 2 atributos, token y usuario
        result.put("token", token);
        result.put("user", usuarioLogueado);

        return result;
    }

    //Login propio con username y password
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PostMapping(value = "/login")
    Map<String, Object> login(@RequestBody Usuario dto){
        Usuario usuarioLogueado = userService.login(dto);
        System.out.println("Usuario logueado: " + usuarioLogueado.getUsuario());
        if(usuarioLogueado == null) return null;
        Map<String, Object> result = new HashMap<>();
        String token = jwtUtil.create(String.valueOf(usuarioLogueado.getId()), usuarioLogueado.getUsuario());
        //Retornar objeto con 2 atributos, token y usuario
        result.put("token", token);
        result.put("user", usuarioLogueado);
        return result;
    }

    //API solo para usuarios propios del sistema, no externos
    /*@PostMapping(value = "/forgotpassword/username={username}")
    public boolean forgotPassword(@PathVariable("username") String username){
        
        //Generar una nueva password
        PasswordGenerator passwordGenerator = new PasswordGenerator.PasswordGeneratorBuilder()
        .useDigits(true).useLower(true).useUpper(true).usePunctuation(true).build();

        String generated_password = passwordGenerator.generate(15);
        //Actualizar el password
        if(userService.forgotPassword(username, generated_password) == false)
            return false;

        //Notificar por correo
        emailService.sendEmailTool("Se ha reiniciado su contraseña a: " + generated_password, username, "Cambio de contraseña");

        return true;
    }*/

    //Excepcion para keyCode third party invalido
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Not authenticated with third party")
    public void conflict() {}

    //Excepcion para data duplicada
    @ExceptionHandler(SQLException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Duplicated data")
    public void conflict2() {}

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/test")
    String test(){
        return "TEST";
    }
    
}
