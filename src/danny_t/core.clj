(ns danny-t.core
  (:require [clojure.pprint :as pprint]
            [meiro.core :as m]
            [meiro.sidewinder :as sw]
            [meiro.aldous-broder :as ab]
            [meiro.wilson :as wilsons]
            [meiro.hunt-and-kill :as hk]
            [meiro.png :as png]
            [meiro.ascii :as ascii]
            [meiro.backtracker :as bktr]
            [meiro.dijkstra :as dijkstra])
  (:gen-class))

;;searchers are threads that start at an intersection, or at the starting pos
;;they need to keep track of the positions they have visited, as well as their parent searcher,
;;so that we can construct the entirety of the path.
;;they end at an intersection, dead end, or the end pos

(def opposite-dirs
  {:north :south
   :south :north
   :west :east
   :east :west})

;;deffed globally for debugging
(def searchers (atom {}))

(defn parallel-solve-v1
  [maze start end]
  (let [pos (get-in maze start)
        result (promise)]
    (reset! searchers {})
    (letfn [;;Compute complete path by repeatedly chaining parent searcher paths until we get to
            ;;the first searcher.
            (end-search
              [final-search-id]
              (let [solution (loop [acc [end]
                                    id final-search-id]
                               (let [{:keys [path parent-id]} (get @searchers id)]
                                 (if parent-id
                                   (recur (concat path acc) parent-id)
                                   (concat path acc))))]
                (deliver result solution)))

            ;;launch a new thread to move through the maze searching for the end coord
            (start-searcher
              [start-coord dir & [parent-id]]
              (let [id (count @searchers)]
                (swap! searchers assoc id {:id id :parent-id parent-id :path [start-coord]})
                (future
                  (loop [cur-coord (m/pos-to dir start-coord)
                         last-dir dir]

                    (if (= cur-coord end)
                      ;;we found it
                      (end-search id)

                      (let [pos (get-in maze cur-coord)
                            ;;can't go back the way we came
                            available-dirs (remove (partial = (opposite-dirs last-dir)) pos)]
                        (cond

                          ;;we have reached an intersection, so start new searchers
                          (< 1 (count available-dirs))
                          (doseq [dir available-dirs]
                            (start-searcher cur-coord dir id))


                          ;;if there is only one direction we can move in, go there
                          (= (count available-dirs) 1)
                          (let [next-dir (first available-dirs)
                                next-coord (m/pos-to next-dir cur-coord)]
                            (swap! searchers update-in [id :path] conj cur-coord)
                            (recur next-coord next-dir))

                          ;;if there is nowhere to go (dead end), thread should die
                          )))))))]

      (if (= (count pos) 1)
        (start-searcher start (first pos))
        (doseq [dir pos]
          (start-searcher start dir)))

      ;;timeout after 10 seconds.
      ;;if you're solving some really crazy mazes, consider bumping this.
      ;;otherwise, something up above probably failed and this will
      ;;never complete
      (deref result 10000 "womp"))))

(defn solve-maze
  [maze]
  (let [bottom-right-coord [(dec (count maze)) (-> maze first count dec)]
        _ (print "Sequential Solve:\n")
        seq-sol (time (dijkstra/solution maze [0 0] bottom-right-coord))
        _ (print "Parallel Solve:\n")
        par-sol (time (parallel-solve-v1 maze [0 0] bottom-right-coord))]))

(defn solve-maze-with-dimensions
  [size maze-algo]
  ;;Except for sidewinder, generating a maze larger than 100x100 cells
  ;;will likely not work
  (let [maze-create-fn (case maze-algo
                         :sidewinder sw/create
                         :aldous-bolder ab/create
                         :wilsons wilsons/create
                         :hunt-and-kill hk/create
                         :recursive-backtracker bktr/create)
        init (m/init size size)
        maze (maze-create-fn init)]
    (solve-maze maze)))
