(ns culture.facts
  "Country-level regional-culture catalog for Nauru (NRU) -- national
  dishes, protected products, beverages, crafts, festivals and heritage
  sites, per ADR-2607171400 addendum 2 (cloud-itonami-municipality-
  culture-catalog Wave 1, in com-junkawasaki/root). Sibling namespace to
  `marketentry.facts` / `statute.facts` (ADR-2607141700); city-level
  counterparts live in the cloud-itonami-municipality-* repos.

  Catalog is keyed by UPPERCASE ISO3 (mirrors `statute.facts`); entries
  carry no :culture/municipality (that attribute is city-level only).

  Nauru is thinly documented on the sources this catalog is allowed to
  cite (Wikipedia English); this catalog reflects only what was actually
  verified there rather than padding to a target count.

  Every entry cites a source URL that was actually fetched and read on
  :culture/retrieved-at -- never fabricated. Summaries state only what the
  cited source confirms. An item not in this table has NO spec-basis, full
  stop; extend `catalog`, do not invent an id/url.")

(def catalog
  "iso3 -> vector of culture entries."
  {"NRU"
   [{:culture/id "nru.dish.coconut-fish"
     :culture/name "Coconut Fish"
     :culture/country "NRU"
     :culture/kind :dish
     :culture/summary "Traditional Nauruan dish of raw fish, often tuna, served in coconut milk with seasonings."
     :culture/url "https://en.wikipedia.org/wiki/Nauruan_cuisine"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "nru.dish.christmas-cake"
     :culture/name "Nauruan Christmas Cake"
     :culture/country "NRU"
     :culture/kind :dish
     :culture/summary "Cake made from banana and coconut, traditionally eaten by Nauruans to celebrate Christmas."
     :culture/url "https://en.wikipedia.org/wiki/Nauruan_cuisine"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "nru.dish.coconut-mousse"
     :culture/name "Coconut Mousse"
     :culture/country "NRU"
     :culture/kind :dish
     :culture/summary "Coconut-based dessert consumed by Nauruans on special occasions."
     :culture/url "https://en.wikipedia.org/wiki/Nauruan_cuisine"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "nru.craft.coconut-fibre-craft"
     :culture/name "Nauruan coconut-fibre craft"
     :culture/country "NRU"
     :culture/kind :craft
     :culture/summary "Nauruan handicraft tradition in which craftsmen weave clothing and fans from coconut fibre and carve articles from coconut-palm wood, using geometric patterns."
     :culture/url "https://en.wikipedia.org/wiki/Culture_of_Nauru"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "nru.festival.angam-day"
     :culture/name "Angam Day"
     :culture/country "NRU"
     :culture/kind :festival
     :culture/summary "Public holiday recognised in the Republic of Nauru, observed annually on October 26 to celebrate the Nauruan people's survival and population recovery."
     :culture/url "https://en.wikipedia.org/wiki/Angam_Day"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "nru.heritage.command-ridge"
     :culture/name "Command Ridge"
     :culture/country "NRU"
     :culture/kind :heritage
     :culture/summary "Site of a Japanese communications bunker built during the World War II occupation of Nauru; rusted WWII guns and artillery remnants remain."
     :culture/url "https://en.wikipedia.org/wiki/Command_Ridge"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}]})

(defn spec-basis [iso3] (get catalog iso3))

(defn coverage
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-nru culture catalog "
                 "(ADR-2607171400 addendum 2, Wave 1): " (count (get catalog "NRU"))
                 " NRU entries, each with a fetched-and-read citation. "
                 "Extend `culture.facts/catalog`, never fabricate an id/url.")})))

(defn by-kind [iso3 kind]
  (filterv #(= (:culture/kind %) kind) (spec-basis iso3)))
