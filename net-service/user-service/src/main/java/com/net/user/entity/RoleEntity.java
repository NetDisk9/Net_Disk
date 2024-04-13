package com.net.user.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Comparator;

@TableName("sys_role")
@Data
public class RoleEntity implements Comparable<RoleEntity>{
    @TableId(value = "role_id")
    private Long roleId;
    private String roleName;
    private String roleCode;
    private Integer roleRank;

    @Override
    public int compareTo(RoleEntity o) {
        return roleRank.compareTo(o.roleRank);
    }
    public static class RoleEntityComparator implements Comparator<RoleEntity>{
        private static RoleEntityComparator instance=null;
        public static RoleEntityComparator getInstance(){
            if(instance==null){
                return instance=new RoleEntityComparator();
            }
            return instance;
        }
        @Override
        public int compare(RoleEntity o1, RoleEntity o2) {
            return o1.roleRank.compareTo(o2.roleRank);
        }
    }
}
