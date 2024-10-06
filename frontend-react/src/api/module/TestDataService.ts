import { TestTypeRest } from "../../models/TestTypeRest"
import { getRequest } from "../AxiosConfig"

type FilterTestData = {
    word?: string
    description?: string
    testTypeRest?: TestTypeRest
    dateStart?: Date
    dateEnd?: Date
    restPagination: RestPagination
}

type RestPagination = {
    sortProperty?: string
    offset?: number
    limit?: number
}

const URL = "/rest/testdata"

export const findAllTestData = async (filters?:FilterTestData) => {
    const other = await getRequest(URL+"/findall", undefined, filters).catch(error => {
        console.log(error)  
    })

    return other
}