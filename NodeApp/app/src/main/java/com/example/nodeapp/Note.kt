package com.example.nodeapp

class Note {
    var nodeID:Int?=null;
    var name:String?=null;
    var nodeDes:String?=null;

    constructor(nodeId:Int, name:String, nodeDes:String){
        this.nodeID = nodeId;
        this.name = name;
        this.nodeDes = nodeDes;
    }
}