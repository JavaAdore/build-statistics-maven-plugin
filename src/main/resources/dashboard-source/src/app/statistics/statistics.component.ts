import {Component, OnInit} from '@angular/core';
import { StatisticsService } from '../services/statistics.service';
import { Unit } from '../../model/unit';
import { Duration } from '../../model/Duration';
import { Constant } from 'src/config/constant';

   
@Component({
  selector: 'statistics',
  templateUrl: './statistics.component.html',
  styleUrls: ['./statistics.component.css']
})
export class StatisticsComponent implements OnInit {
  numberOfUnits:Number=1;
  maxValue;
  selectedUnitIndex:number = 0;
  duration:Duration=new Duration();
  constructor(private statisticsService :StatisticsService){ }
   units:Unit[];

   ngOnInit(){
     this.units = Unit.getUnites();
     this.prepareMaxValue();
     setInterval(() => 
     { document.getElementById('refresh').click();
       this.calcualte()
    
    }, Constant.REFRESH_INTERVAL_IN_MILLISECONDS);

    
    
     }

    prepareMaxValue()
    {
      if(this.selectedUnitIndex==0){
        this.maxValue=30;
      }else{
        this.maxValue=4;
      }
      if(this.numberOfUnits >this.maxValue ){
        this.numberOfUnits = this.maxValue;
      }
        
    }

  calcualte()
  {
     this.duration = this.statisticsService.get(this.numberOfUnits,this.units[this.selectedUnitIndex].id)
      
  }


   
}
