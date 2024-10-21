import { TestTypeRest } from "./TestTypeRest";

export type TestDataRestProps = {
  word?: string;
  date?: Date;
  testTypeRest?: TestTypeRest;
  description?: string;
  number?: number;
};

export type TestDataProps = {
  value: TestDataRestProps;
};
