from flask import jsonify, request, make_response

from custom_exceptions import CustomException
from dependencies import get_pt_service


@get_pt_service
def get_payment_types(service):
    try:
        payment_types = service.get_all()
        return jsonify(payment_types)
    except Exception as e:
        return jsonify({"error": str(e)}), 500


@get_pt_service
def get_payment_type(service, payment_type_id):
    try:
        payment_type = service.get_by_id(payment_type_id)
        return jsonify(payment_type)
    except CustomException as e:
        return jsonify({"error": str(e)}), e.status_code
    except Exception as e:
        return jsonify({"error": str(e)}), 500


@get_pt_service
def delete_payment_type(service, payment_type_id):
    try:
        service.remove_by_id(payment_type_id)
        return make_response("", 204)
    except CustomException as e:
        return jsonify({"error": str(e)}), e.status_code
    except Exception as e:
        return jsonify({"error": str(e)}), 500


@get_pt_service
def create_payment_type(service):
    try:
        req_data = request.get_json()
        payment_type = service.add(req_data)
        return jsonify(payment_type), 201
    except Exception as e:
        return jsonify({"error": str(e)}), 500


@get_pt_service
def edit_payment_type(service, payment_type_id):
    try:
        req_data = request.get_json()
        return service.edit(payment_type_id, req_data)

    except CustomException as e:
        return jsonify({"error": str(e)}), e.status_code

    except Exception as e:
        return jsonify({"error": str(e)}), 500
