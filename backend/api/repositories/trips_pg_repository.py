from psycopg2.extras import RealDictCursor

from repositories.trips_abc_repository import TripsRepository


class TripsPgRepo(TripsRepository):

    def __init__(self, conn):
        self.conn = conn

    def get_yellow_trip_avg_total_amount_by_year_grouped_monthly(self, year):
        with self.conn.cursor(cursor_factory=RealDictCursor) as cursor:
            _qry = "SELECT pu_month, ROUND(AVG(total_amount)::numeric, 2)::float AS avg_amount FROM yellow_trips WHERE pu_year = %s GROUP BY pu_month ORDER BY pu_month"
            cursor.execute(_qry, (year,))
            stats = cursor.fetchall()
            return stats

    def get_yellow_trip_avg_total_amount_by_month_grouped_daily(self, year, month):
        with self.conn.cursor(cursor_factory=RealDictCursor) as cursor:
            _qry = "SELECT pu_day, ROUND(AVG(total_amount)::numeric, 2)::float AS avg_amount FROM yellow_trips WHERE pu_year = %s AND pu_month = %s GROUP BY pu_day ORDER BY pu_day"
            cursor.execute(_qry, (year,month))
            stats = cursor.fetchall()
            return stats

    def get_yellow_trip_avg_total_amount_by_day_grouped_hourly(self, year, month, day):
        with self.conn.cursor(cursor_factory=RealDictCursor) as cursor:
            _qry = "SELECT pu_hour, ROUND(AVG(total_amount)::numeric, 2)::float AS avg_amount FROM yellow_trips WHERE pu_year = %s AND pu_month = %s AND pu_day = %s  GROUP BY pu_hour ORDER BY pu_hour"
            cursor.execute(_qry, (year,month, day))
            stats = cursor.fetchall()
            return stats
