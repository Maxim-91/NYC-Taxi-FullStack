import abc


class YellowTripsService(abc.ABC):
    @abc.abstractmethod
    def get_avg_amount_by_dt_and_step(self, dt, step):
        raise NotImplementedError()