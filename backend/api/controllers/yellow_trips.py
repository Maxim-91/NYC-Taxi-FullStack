from flask import jsonify

from dependencies import get_yt_service


@get_yt_service
def get_avg_amount_by_dt_and_step(service, dt, step):
    try:
        vendors = service.get_avg_amount_by_dt_and_step(dt, step)
        return jsonify(vendors)
    except Exception as e:
        return jsonify({"error": str(e)}), 500