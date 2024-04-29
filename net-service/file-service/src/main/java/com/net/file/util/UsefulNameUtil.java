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
    private int nameNumber;
    private final static String REGEX_SUFFIX="^(?:\\(\\d+\\))?$";

    /**
     * @param list
     * @param name
     */
    public UsefulNameUtil(List<UserFileEntity> list,String name) {
        this.name=new String(name);
        this.nameNumber=0;
        this.nameLen=name.length();
        if(list==null||list.size()==0){
            this.list=new ArrayList<>();
            return;
        }
        name=name.replace("(","\\(");
        name=name.replace(")","\\)");
        String expr=REGEX_SUFFIX.substring(0,1)+name+REGEX_SUFFIX.substring(1);
        System.out.println(expr);
        MatchUtil matchUtil=new MatchUtil(expr);
        this.list=list.stream().filter(userFile -> matchUtil.match(userFile.getFileName()))
                .map(userFile -> solve(userFile.getFileName())).sorted().collect(Collectors.toList());
    }
    public boolean isUseful(String name){
        return !list.contains(solve(name));
    }
    public String getNextName(){
        System.out.println(list);

        for (var number:list){
            if(number==nameNumber){
                nameNumber++;
            }
            else{
                break;
            }
        }
        if(nameNumber==0)
            return name;
        return name+"("+nameNumber+")";
    }

    /**
     * @param name
     * @return {@link Integer }
     */
    private Integer solve(String name){
        int begPos=name.indexOf("(",nameLen),endPos=name.indexOf(")",nameLen);
        if(begPos==-1&&endPos==-1){
            return 0;
        }
        return Integer.parseInt(name.substring(begPos+1,endPos));

    }
}
