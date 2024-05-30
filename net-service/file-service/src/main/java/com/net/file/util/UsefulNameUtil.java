package com.net.file.util;

import com.net.common.util.MatchUtil;
import com.net.file.entity.UserFileEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author sloth
 * @date 2024/04/29
 */
public class UsefulNameUtil {
    private int nameLen;
    private List<Integer> list;
    private String name;
    private String extName;
    private int nameNumber;
    private final static String REGEX_SUFFIX="^(?:\\(\\d+\\))?$";

    /**
     * @param list
     * @param name
     */
    public UsefulNameUtil(List<UserFileEntity> list,String name) {
        String plainName=PathUtil.getPlainName(name);
        this.name=new String(plainName);
        this.nameNumber=0;
        this.nameLen=plainName.length();
        this.extName=PathUtil.getExtName(name);
        System.out.println(extName+"asdsadasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasd");
        if(list==null||list.size()==0){
            this.list=new ArrayList<>();
            return;
        }
        plainName=plainName.replace("(","\\(");
        plainName=plainName.replace(")","\\)");
        String expr=REGEX_SUFFIX.substring(0,1)+plainName+REGEX_SUFFIX.substring(1);
        if(extName!=null){
            expr=concatExtNameExpr(expr,extName.replace(".","\\."));
        }
        MatchUtil matchUtil=new MatchUtil(expr);
        list.stream().forEach(u -> System.out.println(matchUtil.match(u.getFileName())));
        this.list=list.stream()
                .filter(userFile -> matchUtil.match(userFile.getFileName()))
                .map(userFile -> solve(userFile.getFileName())).sorted().collect(Collectors.toList());
    }
    public boolean isUseful(String name){
        return !list.contains(solve(name));
    }
    public String getNextName(){
        System.out.println(list);
        for (var number:list){
            if(number<=nameNumber){
                nameNumber++;
            }
            else{
                break;
            }
        }
        if(nameNumber==0)
            return name+((extName==null)?"":extName);
        return name+"("+nameNumber+")"+((extName==null)?"":extName);
    }

    /**
     * @return {@link Integer }
     */
    private String concatExtNameExpr(String expr,String extName){
        int len=expr.length();
        return expr.substring(0,len-1)+extName+expr.substring(len-1);
    }
    private Integer solve(String name){
        int begPos=name.indexOf("(",nameLen),endPos=name.indexOf(")",nameLen);
        if(begPos==-1&&endPos==-1){
            return 0;
        }
        return Integer.parseInt(name.substring(begPos+1,endPos));

    }
    public static void main(String[] args) {
        UserFileEntity userFile1=new UserFileEntity();
        userFile1.setFileName("test.txt");
        UserFileEntity userFile2=new UserFileEntity();
        userFile2.setFileName("test");
        UserFileEntity userFile3=new UserFileEntity();
        userFile3.setFileName("test(2).mp4");
        List<UserFileEntity> list1 = List.of(userFile1, userFile2, userFile3);
        UsefulNameUtil nameUtil=new UsefulNameUtil(list1,"test.txt");
        System.out.println(nameUtil.getNextName());
    }
}
