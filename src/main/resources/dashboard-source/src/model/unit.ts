import { Constant } from "../config/constant";

export class Unit {

    id : number;
    name:string;


    static getUnites():Unit[]
    {
        var DAY:Unit = {id:Constant.MEASUREMENT_UNIT_DAY_ID,name:'DAY'};
        var WEEK:Unit= {id:Constant.MEASUREMENT_UNIT_WEEK_ID,name:'WEEK'};
        var units =  new Array(DAY, WEEK);
        return units;
    }
}
