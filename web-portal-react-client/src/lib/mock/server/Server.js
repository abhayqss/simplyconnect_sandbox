import UrlPattern from 'url-pattern'

import { referralController, labResearchOrderController } from './controllers'

const RESPONSE_DELAY = 1000

const REQUEST_MAPPING = {
    [referralController.getPath()]: referralController,
    [labResearchOrderController.getPath()]: labResearchOrderController,
}

class Server {
    service(request) {
        return new Promise((resolve, reject) => {
            const { url } = request

            setTimeout(() => {
                for (let path in REQUEST_MAPPING) {
                    const pattern = new UrlPattern(path + '*')

                    if (pattern.match(url)) {
                        resolve(REQUEST_MAPPING[path].handle(request))
                    }
                }
            }, RESPONSE_DELAY)
        })
    }
}

export default new Server()