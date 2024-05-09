package com.net.file.support;

import com.net.common.util.DateFormatUtil;
import com.net.common.util.LongIdUtil;
import com.net.file.entity.UserFileEntity;
import com.net.file.support.ListMap;

import java.util.*;
import java.util.stream.Collectors;

public class UserFileTree {
    private UserFileTreeNode root;

    public UserFileTree(UserFileTreeNode root) {
        this.root = root;
    }
    public void buildTree(List<UserFileEntity> list){
        ListMap<Long,UserFileEntity> map=new ListMap<>();
        for(var entity:list){
            Long pid = entity.getPid();
            map.put(pid,entity);
        }
        buildTree(root,map);
    }
    public void buildTree(UserFileTreeNode node,ListMap<Long,UserFileEntity> map){
        Long nodeId=node.val.getUserFileId();
        map.get(nodeId).forEach(userFile -> {
            UserFileTreeNode child=new UserFileTreeNode(userFile);
            node.addChild(child);
            buildTree(child,map);
        });
    }
    public void rebuildPathByRootPath(){
        rebuildPath(root);
    }
    public void reAssignUserFileIdExceptRoot(){
        reAssignUserFileId(root);
    }
    public List<UserFileEntity> collect(){
        List<UserFileEntity> collector=new ArrayList<>();
        for(var child:root.children){
            collect(collector,child);
        }
        return collector;
    }
    private void collect(List<UserFileEntity> collector,UserFileTreeNode node){
        UserFileEntity now=node.val;
        collector.add(now);
        for(var child:node.children){
            collect(collector,child);
        }
    }
    private void rebuildPath(UserFileTreeNode node){
        UserFileEntity parent=node.val;
        for(var child:node.children){
            UserFileEntity userFile = child.val;
            userFile.setFilePath(parent.getFilePath()+"/"+userFile.getFileName());
            userFile.setUpdateTime(DateFormatUtil.getNow());
            rebuildPath(child);
        }
    }
    private void reAssignUserFileId(UserFileTreeNode node){
        UserFileEntity parent=node.val;
        for(var child:node.children){
            UserFileEntity userFile = child.val;
            userFile.setPid(parent.getUserFileId());
            userFile.setUserFileId(LongIdUtil.createLongId(userFile));
            userFile.setCreateTime(DateFormatUtil.getNow());
            userFile.setUpdateTime(userFile.getCreateTime());
            reAssignUserFileId(child);
        }
    }

    @Override
    public String toString() {
        return "UserFileTree{" +
                "root=" + root +
                '}';
    }

    public static class UserFileTreeNode {
        private UserFileEntity val;
        private LinkedList<UserFileTreeNode> children;

        public UserFileTreeNode(UserFileEntity val) {
            this.val = val;
            children=new LinkedList<>();
        }
        public void addChild(UserFileTreeNode node){
            children.add(node);
        }

        @Override
        public String toString() {
            return "UserFileTreeNode{" +
                    "val=" + val +
                    ", child=" + children +
                    '}';
        }
    }
}
