import { FilterBase } from "../config/FilterBase"
import { RestPagination } from "../config/RestPagination"
import { TestTypeRest } from "./TestTypeRest"

export interface FilterTestData extends FilterBase {
    word?: string
    description?: string
    testType?: number
    dateStart?: string
    dateEnd?: string
}