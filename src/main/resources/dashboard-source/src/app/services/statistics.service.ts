import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http'; 
import { Observable } from 'rxjs';
import { map, timestamp } from 'rxjs/operators';
import { Records } from '../../model/records';
import { Duration } from '../../model/Duration';
import { Constant } from '../../config/constant';

@Injectable({
  providedIn: 'root'
})
export class StatisticsService {

  constructor(private http:HttpClient) {

  }

  get(numberOfUnits,unitType):any
  {
    let result = localStorage.getItem('records');
    result  = JSON.parse(result);
    return this.prepareResult(result,numberOfUnits,unitType);
       

  }
  prepareResult(result: any, numberOfUnits: any, unitTypeId: any): any {
    var timestamps =  Object.keys(result.builds);
    var numberOfDays = unitTypeId==Constant.MEASUREMENT_UNIT_DAY_ID?numberOfUnits : numberOfUnits*7;

    var startDate = new Date();
    startDate.setDate(startDate.getDate()-numberOfDays)
    startDate.setHours(23);
    startDate.setMinutes(59);
    startDate.setSeconds(59)
  
    

    var parentDuration = new Duration();
    var diff = 0;
     timestamps.forEach(timestamp =>{ 
       console.log("--------------------"+startDate.getTime()+" -------- "+ parseFloat(timestamp));
       console.log()
      if(  parseFloat(timestamp) >startDate.getTime()  )
      {
        var build =  result.builds[timestamp];        
        var endDate = build.endDate;
        var milliseconds = parseFloat(endDate)- parseFloat(timestamp);
        diff+=milliseconds;
      }
    }) 
    parentDuration.add(this.getDuration(diff));

    
    
    Object.keys(result.modulesBuildMap).forEach(moduleName=>
      {

         

      var moduleBuilds=  result.modulesBuildMap[moduleName];
      diff=0;
      moduleBuilds.forEach(timestamp => {
       
          if( parseFloat(timestamp)>=startDate.getTime() )
          {
            var build =  result.builds[timestamp];        
            var endDate = build.endDate;
            var milliseconds = parseFloat(endDate)-parseFloat(timestamp);
            diff+=milliseconds;
          }

        });
        var duration = this.getDuration(diff);
        duration.name=moduleName;
        parentDuration.children.push(duration);
      })

    console.log(parentDuration);
    
    return parentDuration;
  
  }

   getDuration(millisec):Duration{
    var seconds:any = (millisec / 1000).toFixed(0);
    var minutes:any = Math.floor(seconds / 60);
    var hours:any = 0;
    if (minutes > 59) {
        hours = Math.floor(minutes / 60);
        hours = (hours >= 10) ? hours : "0" + hours;
        minutes = minutes - (hours * 60);
        minutes = (minutes >= 10) ? minutes : "0" + minutes;
    }

    seconds = Math.floor(seconds % 60);
     var duration = new Duration();
    if (hours != 0) {
      duration.hours=hours;
    }
    duration.minutes = minutes;
    duration.seconds = seconds;
    return duration;
}


 
}

 
   

    