import { UserSetting } from "../models/UserSetting"

const generateDefaultSettings = ():UserSetting => {
    return {
        timeZoneString: "America/Santo_Domingo",
        darkMode: false,
        dateFormat: "dd/MM/yyyy", 
        dateTimeFormat: "dd/MM/yyyy hh:mm a",
        language: "en",
    }
}

export const getSettings = ():UserSetting => {
    var settings = JSON.parse(localStorage.getItem("settings") || "")

    if(settings === "") {
        settings = generateDefaultSettings()
    }

    return settings
}