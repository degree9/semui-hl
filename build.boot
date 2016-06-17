(defn read-file   [file] (read-string (slurp file)))
(defn get-deps    []     (read-file "./dependencies.edn"))
;(defn get-devdeps []     (read-file "./dev_dependencies.edn"))

(set-env!
 :dependencies   (get-deps)
 :resource-paths #{"src"})

(require
 '[adzerk.bootlaces :refer :all]
 '[hoplon.boot-hoplon :refer :all]
 '[boot-semver.core :refer :all]
 '[tolitius.boot-check :as check]
 `[funcool.boot-codeina :refer :all])

(task-options!
 pom    {:project 'degree9/semui-hl
         :version (get-version)
         :description "Semantic UI for Hoplon"
         :url         ""
         :scm {:url ""}}
 apidoc {:version (get-version)
         :reader :clojurescript
         :title "semui-hl"
         :sources #{"target"}
         :description "Semantic UI for Hoplon."})

(deftask tests
  "Run code tests."
  []
  (comp
    (check/with-kibit)
    (check/with-yagni)))

(deftask gen-docs
  "Generate Docs."
  []
  (comp
    (hoplon)
    (sift :to-asset #{#"[.]*.hl"} :invert true)
    (target :dir #{"target"})
    (apidoc)))

(deftask deploy
  "Build project for deployment to clojars."
  []
  (comp
    (version :minor 'inc :patch 'zero)
    (hoplon  :manifest true)
    (build-jar)
    (push-release)))

(deftask dev
  "Build project for development."
  []
  (comp
    (watch)
    (version :no-update true
             :minor 'inc
             :patch 'zero
             :pre-release 'snapshot)
    (hoplon :manifest true)
    (build-jar)))
