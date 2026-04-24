import abc


class VendorsService(abc.ABC):
    @abc.abstractmethod
    def get_all(self):
        raise NotImplementedError()