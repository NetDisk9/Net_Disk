package com.net.gateway.util;

import io.jsonwebtoken.Jwts;
import io.netty.util.internal.StringUtil;

import java.util.Map;

public class JWTUtil {
    private static final String key="NET-DISK-PASSWORD"; //key
//    public static  String getJWT(UserDTO userDTO) {
//        Map map=new HashMap<>();
//        map.put("user",userDTO);
//        String ret = Jwts.builder().
//                signWith(SignatureAlgorithm.HS256,key).  //加密方式H256，密钥为key
//                        setClaims(map).
//                setExpiration(new Date(System.currentTimeMillis()+3600*1000)).  //超时时间
//                        compact();
//        return ret;
//    }

    public static String parseJWT(String plainText) throws Exception{
        String userDTO=null;
            if(StringUtil.isNullOrEmpty(plainText))
                throw new Exception();
            Map map=(Map) Jwts.parser().setSigningKey(key).parseClaimsJws(plainText).getBody(); //取出user数据，由于user是对象，会在内部被封装为map。如果是基础类型，应该会被封装为String吧大概

        return (String) map.get("userid");
    }
//    public static void saveJWT(UserDTO userDTO){
//        try {
//            String JWTCode = getJWT(userDTO);
//            BufferedWriter writer = new BufferedWriter(new FileWriter(new File("./token.txt")));
//            writer.write(JWTCode);
//            writer.flush();
//            writer.close();
//        }
//        catch (Exception e){
//            System.out.println(e);
//        }
//    }
//    public static UserDTO getJWT(){
//        UserDTO userDTO=null;
//        try{
//            BufferedReader reader = new BufferedReader(new FileReader(new File("./token.txt")));
//            String JWTCode=reader.readLine();
//            userDTO=parseJWT(JWTCode);
//            reader.close();
//        }
//        catch (Exception e){
//            System.out.println(e);
//        }
//        return userDTO;
//    }
}