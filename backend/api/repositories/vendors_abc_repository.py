import abc


class VendorsRepository(abc.ABC):
    @abc.abstractmethod
    def get_all(self):
        raise NotImplementedError()