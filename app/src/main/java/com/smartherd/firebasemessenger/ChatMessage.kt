package com.smartherd.firebasemessenger

class ChatMessage(val id:String, val text:String, val fromId:String, val toId:String, val timesLong:Long){
    constructor() : this("", "", "", "", -1)
}