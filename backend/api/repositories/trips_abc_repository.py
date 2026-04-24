import abc


class TripsRepository(abc.ABC):
    @abc.abstractmethod
    def get_yellow_trip_avg_total_amount_by_year_grouped_monthly(self, year):
        raise NotImplementedError()

    @abc.abstractmethod
    def get_yellow_trip_avg_total_amount_by_month_grouped_daily(self, year, month):
        raise NotImplementedError()

    @abc.abstractmethod
    def get_yellow_trip_avg_total_amount_by_day_grouped_hourly(self, year, month, day):
        raise NotImplementedError()