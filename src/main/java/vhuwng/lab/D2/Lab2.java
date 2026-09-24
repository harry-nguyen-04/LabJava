package vhuwng.lab.D2;

import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import vhuwng.lab.D2.dto.CountByStatusDto;
import vhuwng.lab.D2.dto.RevenueByCustomerDto;
import vhuwng.lab.D2.dto.RevenueByDayDto;
import vhuwng.lab.D2.dto.SampleQueryDto;
import vhuwng.lab.D2.dto.SampleQueryFilterDto;
import vhuwng.lab.D2.dto.SumOfTotalByStatus;
import vhuwng.lab.D2.dto.TotalsDto;
import vhuwng.lab.D2.service.OrderTotalCalculator;
import vhuwng.lab.D2.service.OrderUtil;

@SpringBootApplication
public class Lab2 implements CommandLineRunner {
    private final OrderTotalCalculator orderTotalCalculator;
    private final OrderUtil orderUtil;
    public Lab2(OrderTotalCalculator orderTotalCalculator, OrderUtil orderUtil) {
        this.orderTotalCalculator = orderTotalCalculator;
        this.orderUtil = orderUtil;
    }

    public static void main(String[] args) {
        SpringApplication.run(Lab2.class, args);
    }

    public void run(String[] args) {
        String qStatus = null;
        LocalDate qFrom = null;
        LocalDate qTo = null;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--status" -> qStatus = args[++i];
                case "--from" -> qFrom = LocalDate.parse(args[++i]);
                case "--to" -> qTo = LocalDate.parse(args[++i]);
            }
        }


        SampleQueryFilterDto filter = new SampleQueryFilterDto(qStatus, qFrom, qTo);
        TotalsDto totals = orderTotalCalculator.calcTotals(filter);
        CountByStatusDto countByStatus = orderTotalCalculator.calcCountByStatus(filter);
        SumOfTotalByStatus sumOfTotalByStatus = orderTotalCalculator.calcSumOfTotalByStatus(filter);
        RevenueByDayDto revenueByDay = orderTotalCalculator.calcRevenueByDay(filter);
        List<RevenueByCustomerDto> revenueByCustomer = orderTotalCalculator.calcRevenueByCustomer(filter);
        SampleQueryDto sampleQuery = orderTotalCalculator.calcSampleQuery(filter);
        System.out.println(orderUtil.prettyJson(totals));
        System.out.println(orderUtil.prettyJson(countByStatus));
        System.out.println(orderUtil.prettyJson(sumOfTotalByStatus));
        System.out.println(orderUtil.prettyJson(revenueByDay));
        System.out.println(orderUtil.prettyJson(revenueByCustomer));
        System.out.printf(
            "sampleQuery: status=%s, from=%s, to=%s, orderCount=%d, "
                    + "sumOfTotal=%d, orderCodes=%s%n",
            sampleQuery.filter().status(),
            sampleQuery.filter().from(),
            sampleQuery.filter().to(),
            sampleQuery.orderCount(),
            sampleQuery.sumOfTotal(),
            sampleQuery.orderCodes()
        );
    }
}