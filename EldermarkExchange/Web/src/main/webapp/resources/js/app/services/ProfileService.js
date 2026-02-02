define(
    [
        'underscore',
        path('./BaseService')
    ],
    function (_, BaseService) {
        function ProfileService() {
            BaseService.apply(this);
        }

        ProfileService.prototype = Object.create(BaseService.prototype);
        ProfileService.prototype.constructor = ProfileService;


        ProfileService.prototype.find = function () {
            return this.request({
                url: '/profile/common-profile-info'
            });
        };

        return ProfileService;
    }
);