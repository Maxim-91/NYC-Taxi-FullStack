from flask import jsonify, make_response, request

from custom_exceptions import CustomException
from dependencies import get_rt_service


@get_rt_service
def get_rate_codes(service):
    try:
        rate_codes = service.get_all()
        return jsonify(rate_codes)
    except Exception as e:
        return jsonify({"error": str(e)}), 500


@get_rt_service
def get_rate_code(service, rate_code_id):
    try:
        rate_code = service.get_by_id(rate_code_id)
        return jsonify(rate_code)
    except CustomException as e:
        return jsonify({"error": str(e)}), e.status_code
    except Exception as e:
        return jsonify({"error": str(e)}), 500


@get_rt_service
def delete_rate_code(service, rate_code_id):
    try:
        service.remove_by_id(rate_code_id)
        return make_response("", 204)
    except CustomException as e:
        return jsonify({"error": str(e)}), e.status_code
    except Exception as e:
        return jsonify({"error": str(e)}), 500


@get_rt_service
def create_rate_code(service):
    try:
        req_data = request.get_json()
        payment_type = service.add(req_data)
        return jsonify(payment_type), 201
    except Exception as e:
        return jsonify({"error": str(e)}), 500


@get_rt_service
def edit_rate_code(service, rate_code_id):
    try:
        req_data = request.get_json()
        return service.edit(rate_code_id, req_data)

    except CustomException as e:
        return jsonify({"error": str(e)}), e.status_code

    except Exception as e:
        return jsonify({"error": str(e)}), 500