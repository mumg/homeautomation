package net.muratov.mercury200;

import net.muratov.mercury200.messages.MQElectricityConsumption;
import net.muratov.mercury200.messages.MQElectricityStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component()
@Scope(value = "singleton")
public class Mercury200Scheduler {

    Logger logger = Logger.getLogger("scheduler");

    @Autowired
    Mercury200Connector mConnector;

    @Autowired
    Mercury200MQDispatcher mDispatcher;

    @Value("${MQ.notify}")
    private String mNotifyQueue;

    int convert ( byte b ){
        return (b & 0x0000000F) + ((b & 0x000000F0) >> 4) * 10;
    }

    double convert ( byte b [] , int index ){
        return convert(b[index * 4 + 0]) * 10000.0 +  convert(b[index * 4 + 1]) * 100.0 +
                convert(b[index * 4 + 2]) + convert(b[index * 4 + 3])/ 100.0 ;
    }

    @Scheduled(fixedRate = 60000)
    public void schedule(){

        try {

            byte [] status = mConnector.request(0x63, 7);

            double voltage = convert(status[0]) * 10.0 +
                             convert(status[1]) / 10.0;
            double amperage = convert(status[2]) +
                              convert(status[3]) / 100.0;

            double power = convert(status[4]) * 10000.0 +
                            convert(status[5]) * 100.0 +
                            convert(status[6]);

            MQElectricityStatus status_cmd = new MQElectricityStatus(voltage, amperage, power);

            logger.info("Status: voltage = " + Double.toString(voltage) + " amperage = " + Double.toString(amperage) + " power = " + power);


            mDispatcher.request(mNotifyQueue, status_cmd);

            byte [] consumption = mConnector.request(0x27, 16);

            for ( int index = 0; index < 4; index ++){
                MQElectricityConsumption consumption_cmd = new MQElectricityConsumption(index, convert(consumption, index));
                if ( consumption_cmd.Value > 0.0 ){
                    logger.info("tariff: " + Integer.toString(index) + " consumption: " + Double.toString(consumption_cmd.Value));
                     mDispatcher.request(mNotifyQueue, consumption_cmd);
                }

            }



        }catch (Exception e){
            logger.info(e.getMessage());
        }

    }
}
