(ns terrain.routes.data
  (:use [common-swagger-api.schema]
        [ring.util.http-response :only [ok]]
        [terrain.services.sharing :only [share unshare]]
        [terrain.auth.user-attributes]
        [terrain.routes.schemas.filetypes]
        [terrain.util]
        [terrain.util.transformers :only [add-current-user-to-map]])
  (:require [terrain.util.config :as config]
            [terrain.clients.data-info :as data]
            [terrain.clients.saved-searches :as saved]))

(defn secured-data-routes
  "The routes for data sharing endpoints."
  []
  (optional-routes
   [config/data-routes-enabled]

   (context "/filetypes" []
     :tags ["data"]

     (POST "/type" []
       :summary "Set or Remove File Type Labels"
       :body [body (describe FileType "The file type to set")]
       :description-file "docs/post-filetypes.md"
       (ok (data/set-file-type (add-current-user-to-map {}) body)))

     (GET "/type-list" [:as req]
       :summary "List Supported File Type Labels"
       :return TypesList
       :description "Lists the file type labels supported by the Discovery Environment."
       (ok (data/get-type-list))))

   (POST "/share" [:as req]
         (share req))

   (POST "/unshare" [:as req]
         (unshare req))

   (GET "/saved-searches" []
        (saved/get-saved-searches (:username current-user)))

   (POST "/saved-searches" [:as {:keys [body]}]
         (saved/set-saved-searches (:username current-user) body))

   (DELETE "/saved-searches" []
           (saved/delete-saved-searches (:username current-user)))))
