from psycopg2.extras import RealDictCursor

from custom_exceptions import CustomException
from repositories.rt_abc_repository import RtRepository


class RtPgRepository(RtRepository):

    def __init__(self, conn):
        self.conn = conn

    def get_all(self):
        with self.conn.cursor(cursor_factory=RealDictCursor) as cursor:
            cursor.execute("SELECT * FROM rate_codes")
            return cursor.fetchall()

    def get_by_id(self, rate_code_id):
        with self.conn.cursor(cursor_factory=RealDictCursor) as cursor:
            cursor.execute('SELECT * FROM rate_codes WHERE "RatecodeID"  = %s', (rate_code_id,))
            item = cursor.fetchone()
            if item is None:
                raise CustomException("Rate code not found", 404)

            return item


    def remove_by_id(self, rate_code_id):
        with self.conn.cursor(cursor_factory=RealDictCursor) as cursor:
            try:
                cursor.execute('DELETE FROM rate_codes WHERE "RatecodeID" = %s', (rate_code_id,))
                self.conn.commit()
            except Exception as e:
                self.conn.rollback()
                raise e

    def add(self, req_data):
        with self.conn.cursor(cursor_factory=RealDictCursor) as cursor:
            try:
                cursor.execute('INSERT INTO rate_codes(code) VALUES(%s) RETURNING "RatecodeID"',
                               (req_data['code'],))
                self.conn.commit()
                return cursor.fetchone()['RatecodeID']
            except Exception as e:
                self.conn.rollback()
                raise e

    def edit(self, rate_code_id, req_data):
        with self.conn.cursor(cursor_factory=RealDictCursor) as cursor:
            try:
                cursor.execute('UPDATE rate_codes SET code = %s WHERE "RatecodeID" = %s',
                               (req_data['code'], rate_code_id))
                self.conn.commit()
                return req_data
            except Exception as e:
                self.conn.rollback()
                raise e