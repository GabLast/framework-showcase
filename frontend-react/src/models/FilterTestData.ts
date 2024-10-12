import { FilterBase } from "./FilterBase"
import { RestPagination } from "./RestPagination"
import { TestTypeRest } from "./TestTypeRest"

export interface FilterTestData extends FilterBase {
    word?: string
    description?: string
    testType?: number
    dateStart?: string
    dateEnd?: string
}