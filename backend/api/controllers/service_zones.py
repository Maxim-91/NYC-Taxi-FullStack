from flask import jsonify

from dependencies import get_sz_service


@get_sz_service
def get_service_zones(service):
    try:
        service_zones = service.get_all()
        return jsonify(service_zones)
    except Exception as e:
        return jsonify({"error": str(e)}), 500