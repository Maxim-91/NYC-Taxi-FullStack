import datetime

from custom_exceptions import CustomException
from repositories.trips_abc_repository import TripsRepository
from services.yellow_trips_abc_service import YellowTripsService


class YellowTripsLocalDBService(YellowTripsService):
    def __init__(self, repo: TripsRepository):
        self.repo = repo

    def get_avg_amount_by_dt_and_step(self, dt, step):
        ts = datetime.datetime.fromtimestamp(int(dt) // 1000)
        stats = []
        if step.lower() == "year":
            stats = self.repo.get_yellow_trip_avg_total_amount_by_year_grouped_monthly(ts.year)
        elif step.lower() == "month":
            stats = self.repo.get_yellow_trip_avg_total_amount_by_month_grouped_daily(ts.year, ts.month)
        elif step.lower() == "day":
            stats = self.repo.get_yellow_trip_avg_total_amount_by_day_grouped_hourly(ts.year, ts.month, ts.day)
        else:
            raise CustomException("Invalid step", 400)
        return stats