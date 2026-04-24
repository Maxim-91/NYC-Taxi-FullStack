from psycopg2.extras import RealDictCursor

from repositories.vendors_abc_repository import VendorsRepository


class VendorsPgRepository(VendorsRepository):


    def  __init__(self, conn):
        self.conn = conn

    def get_all(self):
        with self.conn.cursor(cursor_factory=RealDictCursor) as cursor:
            cursor.execute("SELECT * FROM vendors")
            return cursor.fetchall()