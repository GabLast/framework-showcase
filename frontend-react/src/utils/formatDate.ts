import { format } from "date-fns";
import { getSettings } from "../session/SessionService";


export const formatDate = (date?: Date) => {
    if(date === undefined) {
        return ""
    }

    return format(date, "dd/MM/yyyy hh:mm a")
}