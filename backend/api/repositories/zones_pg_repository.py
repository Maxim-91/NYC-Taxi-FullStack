from psycopg2.extras import RealDictCursor

from repositories.zones_abc_repository import ZonesRepository


class ZonesPgRepository(ZonesRepository):

    def __init__(self, conn):
        self.conn = conn

    def get_all(self):
        with self.conn.cursor(cursor_factory=RealDictCursor) as cursor:
            cursor.execute('SELECT "LocationID", '
                           'zone_name, '
                           'borough_name, '
                           'service_zone_name '
                           'FROM zones AS z '
                           'INNER JOIN boroughs AS b ON z.borough_id = b.id '
                           'INNER JOIN service_zones AS sz ON sz.id = z.service_zone_id')
            zones = cursor.fetchall()
            return zones