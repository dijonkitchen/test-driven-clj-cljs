(defn setup-boot-environment []
  (set-env!
   :resource-paths #{"resources"}
   :source-paths  #{"boot_tasks" "src"}
   :dependencies '[
                   [clj-http "3.7.0"]
                   [com.stuartsierra/component "0.3.2"]
                   [com.taoensso/timbre "4.10.0"]
                   [eftest "0.3.2"]
                   [me.raynes/fs "1.4.6"]
                   [org.clojure/clojure "1.9.0-beta2"]
                   [org.clojure/clojurescript "1.9.946"]
                   [ring "1.6.2"]

                   ;; Dev/Test
                   [adzerk/boot-cljs-repl "0.3.3" :scope "test"]
                   [adzerk/boot-cljs "2.1.4" :scope "test"]
                   [clj-webdriver "0.7.2"]
                   [com.cemerick/piggieback "0.2.1"  :scope "test"]
                   [jonase/eastwood "0.2.5"
                    :exclusions [org.clojure/clojure]
                    :scope "test"
                    ]
                   [jonase/kibit "0.1.5" :scope "test"]
                   [karma-reporter "3.0.0-alpha1" :scope "test"]
                   [org.clojure/tools.namespace "0.2.11" :scope "test"]
                   [org.clojure/tools.nrepl "0.2.12" :scope "test"]
                   [org.seleniumhq.selenium/selenium-htmlunit-driver
                    "2.52.0"
                    :scope "test"
                    ]
                   [weasel "0.7.0" :scope "test"]
                   ]))

(defn clear-aliases []
  (ns-unalias 'boot.user 'my-app)
  (ns-unalias 'boot.user 'dev))

(defn setup-working-namespaces []
  (println "Setting up working namespaces...")
  (setup-boot-environment)
  (clear-aliases)
  (require
   '[my-app.boot-tasks.core :as my-app]
   '[my-app.build.dev :as dev])
  (println "Ready!"))

(defn show-classpath []
  (let [class-path (get-env :boot-class-path)
        class-path-vector (clojure.string/split class-path #":")]
    (clojure.pprint/pprint class-path-vector)))

(deftask check-conflicts
  "Verify there are no dependency conflicts."
  []
  (with-pass-thru fs
    (require '[boot.pedantic :as pedant])
    (require '[boot.pod :as pod])
    (let [dep-conflicts (resolve 'pedant/dep-conflicts)]
      (if-let [conflicts (not-empty (dep-conflicts (resolve 'pod/env)))]
        (throw (ex-info (str "Unresolved dependency conflicts. "
                             "Use :exclusions to resolve them!")
                        conflicts))
        (println "\nVerified there are no dependency conflicts.")))))

(setup-working-namespaces)
