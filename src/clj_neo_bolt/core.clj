(ns clj-neo-bolt.core
  (:import (org.neo4j.driver.v1 AuthTokens Config Driver
                                GraphDatabase Record Session
                                StatementResult Values)
           (java.util Map))
  (:gen-class))

(defn get-driver
  (^Driver [^String url]
   (GraphDatabase/driver url))
  (^Driver [^String url ^String username ^String password]
   (GraphDatabase/driver url (AuthTokens/basic username password)))
  (^Driver [^String url ^String username ^String password ^Config config]
   (GraphDatabase/driver url (AuthTokens/basic username password) config)))

(defn get-session
  ^Session [^Driver driver]
  (.session driver))

(defn run-query
  ([^Session session ^String qry]
   (run-query session qry {}))
  ([^Session session ^String qry ^Map params]
   (map (fn [^Record r]
          (into {} (.asMap r)))
        (iterator-seq (.run session qry params)))))

;; Docs: http://neo4j.com/docs/developer-manual/3.0/drivers/
;; http://neo4j.com/docs/api/java-driver/current/

(defn run-example
  []
  (with-open [driver  (get-driver "bolt://localhost")]
    (with-open [session (get-session driver)]
      (run-query session "CREATE (a:Person {name:'Arthur', title:'King'})")
      (doseq [r  (run-query session
                            "MATCH (a:Person) WHERE a.name = {name}
                             RETURN a.name AS name, a.title AS title"
                            {"name" "Arthur"})]
        (println r)))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (run-example))
