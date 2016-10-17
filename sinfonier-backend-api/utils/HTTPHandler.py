import requests


class HTTPHandler:
    @staticmethod
    def get(url, retrytimes=0,*args, **kvargs):
        '''
        >>> res = HTTPHandler.get("https://httpbin.org/get",retrytimes=25)
        >>> 'httpbin.org' in res
        True
        '''
        return HTTPHandler._get(url,retrytimes=retrytimes,*args,**kvargs).text

    @staticmethod
    def getJson(url,retrytimes=0,*args,**kvargs):
        try:
            return HTTPHandler._get(url,retrytimes=retrytimes,*args,**kvargs).json()
        except requests.exceptions.HTTPError as r:
            return "{ error: "+str(r.status_code)+"}"

    @staticmethod
    def post(url,*args,**kvargs):
        r = requests.post(url*args,**kvargs)
        r.raise_for_status()
        return r.text

    @staticmethod
    def postJson(url,*args,**kvargs):
        try:
            r = requests.post(url,*args,**kvargs)
            r.raise_for_status()
            return r.json()
        except requests.exceptions.HTTPError as r:
            return "{ error: "+str(r.status_code)+"}"

    @staticmethod
    def delete(url,*args,**kvargs):
        r = requests.delete(url,*args,**kvargs)
        r.raise_for_status()
        return r.text


    @staticmethod
    def _get(url,retrytimes=0,*args,**kvargs):
        r = requests.get(url,*args,**kvargs)

        if retrytimes > 0 and r.status_code != requests.codes.ok:
            for i in range(0, retrytimes):
                r = requests.get(url,*args,**kvargs)
                if r.status_code == requests.codes.ok:
                    break

        r.raise_for_status()
        return r
