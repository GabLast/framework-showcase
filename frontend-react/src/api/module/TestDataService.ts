import { fi } from "date-fns/locale";
import { TestTypeRest } from "../../models/TestTypeRest";
import { getRequest, postRequest } from "../AxiosConfig";
import { FilterTestData } from "../../models/FilterTestData";

const URL = "/rest/testdata";

export const findAllTestData = async (filters?: FilterTestData) => {

  const other = await getRequest(URL + "/findall", filters).catch((error) => {
    console.log(error);
  });

  return other;
};
