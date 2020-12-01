/**
 *  broadlink-ac
 *
 *  Copyright 2020 Gogu Programatoru
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 */
metadata {
	definition (name: "broadlink-ac", namespace: "goguprogramatoru", author: "Gogu Programatoru", cstHandler: true) {
		capability "Air Conditioner Mode"
		capability "Switch"
		capability "Temperature Measurement"
		capability "Thermostat Cooling Setpoint"
        capability "Refresh" 
	}


	simulator {
	}

	tiles {
	}
}

def parse(String description) {
	log.debug "Parsing '${description}'"
}

def autoRefresh(){
    refresh()
}

def refresh() {
    getStatus(getAcMac())
}

def setAirConditionerMode(mode) {
	String acMode = ""
    switch(mode){
        case "cool":
        	acMode = "COOLING"
            break
        case "heat":
        	acMode = "HEATING"
        	break
        case "dry":
        	acMode = "DRY"
        	break
        case "fanOnly":
        	acMode = "FAN"
        	break
        case "auto":
        	acMode = "AUTO"
        	break
        default:
            println("mode not implemented")
    }
	String command = '{"command":"mode","param":"'+acMode+'"}'
	boolean result = executeAcCommand(getAcMac(),command)
    if(result == true){
        refresh()
    }
}

def on() {
	String command = '{"command":"power","param":"on"}'
	boolean result = executeAcCommand(getAcMac(),command)
    if(result == true){
        refresh()
    }
}

def off() {
	String command = '{"command":"power","param":"off"}'
	boolean result = executeAcCommand(getAcMac(),command)
    if(result == true){
        refresh()
    }
}

def setCoolingSetpoint(setpoint) {
    String command = '{"command":"temperature","param":"'+setpoint+'"}'
	boolean result = executeAcCommand(getAcMac(),command)
    if(result == true){
        refresh()
    }
}

private boolean executeAcCommand(String mac, String command){
    String path = "/ac/command"
    String deviceIp = getServerIp()
    String devicePort = getServerPort()
    
    
    def headers = [:] 
    headers.put("HOST", "$deviceIp:$devicePort")
	headers.put("Authorization", "Bearer mare-secret")
    headers.put("x-device-mac", mac)
	headers.put("Content-Type", "application/json")


    try {
		def hubAction = new physicalgraph.device.HubAction(
			method: "POST",
			path: path,
			body: command,
			headers: headers
			)
		hubAction.options = [outputMsgToS3:false]
		sendHubCommand(hubAction)
	}
	catch (Exception e) {
		log.error "Hit Exception $e on $hubAction"
        return false
	}
    
    return true

}

def getStatus(String mac){
	String path = "/ac/status"
    String deviceIp = getServerIp()
    String devicePort = getServerPort()
    
    def headers = [:] 
    headers.put("HOST", "$deviceIp:$devicePort")
	headers.put("Authorization", "Bearer mare-secret")
    headers.put("x-device-mac", mac)
    
    def httpRequest = [
        path: path,
        method: "GET",
        headers: headers
    ]
    
    try {
        def hubAction = new physicalgraph.device.HubAction(httpRequest, null, [callback: handleStatus])
        return sendHubCommand(hubAction)
    }
    catch (Exception e) {
        log.debug "Hit Exception $e on $hubAction"
    }
}

    
    
def handleStatus(output) {
    def body = output.body
    def status = output.status

    if (status == 200) {
        body = new groovy.json.JsonSlurper().parseText(body)
        if(body["power"] == "OFF"){
        	sendEvent(name: "switch", value: "off")
        }
        else {
        	sendEvent(name: "switch", value: "on")
        }
        
        sendEvent(name: "temperature", value: body["ambient_temp"])
        sendEvent(name: "coolingSetpoint", value: body["temp"])
        
        String acMode = body["mode"]
        String stMode = ""
        switch(acMode){
        	case "COOLING":
            	stMode = "cool"
            	break
            case "DRY":
            	stMode = "dry"
            	break
            case "HEATING":
            	stMode = "heat"
            	break
            case "FAN":
            	stMode = "fanOnly"
            	break
            case "AUTO":
            	stMode = "auto"
            	break
            default:
            	stMode = "notSupported"
        
        }
        
        sendEvent(name: "airConditionerMode", value: stMode)
        
    }
    else {
        log.debug "Unable to get status"
    }
}

def getServerIp(){
    return device.deviceNetworkId.split("\\|")[0]
}

def getServerPort(){
    return device.deviceNetworkId.split("\\|")[1]
}

def getAcMac(){
    return device.deviceNetworkId.split("\\|")[2]
}
