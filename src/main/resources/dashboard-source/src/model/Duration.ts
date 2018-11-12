export class Duration {

    public name:string;

    public hours:number =0;
    public minutes:number = 0;
    public seconds:number =0;

     children: Duration[]= [];

    add(durtaion:Duration):Duration
    {
        if(durtaion.hours!=null)
        {
            this.hours += durtaion.hours;     
        }

        if(durtaion.minutes!=null)
        {
            this.minutes += durtaion.minutes;     
        }


        if(durtaion.seconds!=null)
        {
            this.seconds += durtaion.seconds;   
         }

        return this;
    }

   
}
