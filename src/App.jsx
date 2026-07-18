import "./App.css";
import { useState, useEffect, useRef } from "react";

// --- Local Storage Caching Configuration ---
const CACHE_KEY = "nexastore_products_cache";
const CACHE_TIME_KEY = "nexastore_products_cache_time";
const CACHE_DURATION = 60 * 60 * 1000; // 1 hour in milliseconds

const getCachedData = () => {
  try {
    const cached = localStorage.getItem(CACHE_KEY);
    const cachedTime = localStorage.getItem(CACHE_TIME_KEY);
    if (cached && cachedTime) {
      if (Date.now() - parseInt(cachedTime) < CACHE_DURATION) {
        return JSON.parse(cached);
      }
    }
  } catch (e) {
    console.error("Failed to load cached products:", e);
  }
  return null;
};

const setCachedData = (data) => {
  try {
    localStorage.setItem(CACHE_KEY, JSON.stringify(data));
    localStorage.setItem(CACHE_TIME_KEY, Date.now().toString());
  } catch (e) {
    console.error("Failed to cache products:", e);
  }
};

// --- Category Normalization Helper ---
const normalizeCategory = (apiCategory) => {
  const cat = apiCategory.toLowerCase();
  if (cat === "smartphones" || cat === "mobiles") return "Mobiles";
  if (cat === "laptops") return "Laptops";
  if (cat === "audio" || cat === "mobile-accessories") return "Audio";
  if (
    cat.includes("dress") ||
    cat.includes("shirt") ||
    cat.includes("shoe") ||
    cat.includes("watch") ||
    cat.includes("bag") ||
    cat.includes("jewel") ||
    cat.includes("clothing") ||
    cat.includes("fashion") ||
    cat === "tops"
  ) {
    return "Fashion";
  }
  // Beautify category string
  return apiCategory
    .replace(/-/g, " ")
    .replace(/\b\w/g, (char) => char.toUpperCase());
};

// --- Advanced Search Parser ---
const parseSearchQuery = (query, items) => {
  if (!query) return items;

  const cleanQuery = query.toLowerCase().trim();

  // 1. Check for price limits (e.g. "under 50000", "below 1000", "less than 500", "above 10000", "over 2000")
  const priceUnderRegex = /(?:under|below|less\s+than)\s*(\d+)/i;
  const priceAboveRegex = /(?:above|over|more\s+than)\s*(\d+)/i;

  const underMatch = cleanQuery.match(priceUnderRegex);
  const aboveMatch = cleanQuery.match(priceAboveRegex);

  // 2. Check for ratings (e.g. "rating above 4.5", "rating over 4", "rated above 4.5")
  const ratingAboveRegex = /(?:rating|rated|stars?)\s*(?:above|over|more\s+than|>\s*)(\d+(?:\.\d+)?)/i;
  const ratingBelowRegex = /(?:rating|rated|stars?)\s*(?:below|under|less\s+than|<\s*)(\d+(?:\.\d+)?)/i;

  const ratingAboveMatch = cleanQuery.match(ratingAboveRegex);
  const ratingBelowMatch = cleanQuery.match(ratingBelowRegex);

  // 3. Check for discount limits (e.g. "discount above 20%", "discount over 15%", "off above 10%")
  const discountAboveRegex = /(?:discount|off)\s*(?:above|over|more\s+than|>\s*)(\d+)/i;
  const discountAboveMatch = cleanQuery.match(discountAboveRegex);

  // Extract textual keywords by removing constraint matches from the query
  let textQuery = cleanQuery;
  if (underMatch) textQuery = textQuery.replace(underMatch[0], "");
  if (aboveMatch) textQuery = textQuery.replace(aboveMatch[0], "");
  if (ratingAboveMatch) textQuery = textQuery.replace(ratingAboveMatch[0], "");
  if (ratingBelowMatch) textQuery = textQuery.replace(ratingBelowMatch[0], "");
  if (discountAboveMatch) textQuery = textQuery.replace(discountAboveMatch[0], "");

  textQuery = textQuery.trim().replace(/\s+/g, " ");
  const keywords = textQuery.split(" ").filter((w) => w.length > 0);

  return items.filter((p) => {
    // Price constraint check
    if (underMatch) {
      const maxPrice = parseFloat(underMatch[1]);
      if (p.price > maxPrice) return false;
    }
    if (aboveMatch) {
      const minPrice = parseFloat(aboveMatch[1]);
      if (p.price < minPrice) return false;
    }

    // Rating constraint check
    if (ratingAboveMatch) {
      const minRating = parseFloat(ratingAboveMatch[1]);
      if (p.rating < minRating) return false;
    }
    if (ratingBelowMatch) {
      const maxRating = parseFloat(ratingBelowMatch[1]);
      if (p.rating > maxRating) return false;
    }

    // Discount constraint check
    if (discountAboveMatch) {
      const minDiscount = parseFloat(discountAboveMatch[1]);
      if (p.discount < minDiscount) return false;
    }

    // Keyword matching
    if (keywords.length > 0) {
      return keywords.every((kw) => {
        return (
          p.name.toLowerCase().includes(kw) ||
          p.brand.toLowerCase().includes(kw) ||
          p.category.toLowerCase().includes(kw) ||
          p.description.toLowerCase().includes(kw)
        );
      });
    }

    return true;
  });
};

function App() {
  // Core datasets & loading state
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);

  // Layout and interaction states
  const [search, setSearch] = useState("");
  const [category, setCategory] = useState("All");
  const [cart, setCart] = useState([]);
  const [wishlist, setWishlist] = useState([]);
  const [darkMode, setDarkMode] = useState(false);

  // Sidebar Filter & Sort states
  const [selectedBrands, setSelectedBrands] = useState([]);
  const [minPrice, setMinPrice] = useState("");
  const [maxPrice, setMaxPrice] = useState("");
  const [minRating, setMinRating] = useState(0);
  const [onlyInStock, setOnlyInStock] = useState(false);
  const [minDiscount, setMinDiscount] = useState(0);
  const [sortBy, setSortBy] = useState("Featured");
  const [mobileFilterOpen, setMobileFilterOpen] = useState(false);

  // Pagination / Scroll state
  const [visibleCount, setVisibleCount] = useState(20);

  // --- Fetch API Integration & Normalization ---
  useEffect(() => {
    const getProducts = async () => {
      setLoading(true);
      const cached = getCachedData();
      if (cached) {
        setProducts(cached);
        setLoading(false);
        return;
      }

      try {
        // Fetch DummyJSON products (limit=200 gets all available mock items)
        const dummyRes = await fetch("https://dummyjson.com/products?limit=200");
        const dummyData = await dummyRes.json();
        const dummyList = dummyData.products || [];

        // Fetch FakeStore products
        let fakeList = [];
        try {
          const fakeRes = await fetch("https://fakestoreapi.com/products");
          fakeList = await fakeRes.json();
        } catch (err) {
          console.error("FakeStore API request failed:", err);
        }

        const usedImages = new Set();
        const mergedList = [];

        // Normalize DummyJSON products
        for (const p of dummyList) {
          let img = p.thumbnail || (p.images && p.images[0]);
          // Deduplicate images by searching alternative images in product images array
          if (usedImages.has(img) && p.images) {
            for (const altImg of p.images) {
              if (!usedImages.has(altImg)) {
                img = altImg;
                break;
              }
            }
          }

          if (!usedImages.has(img) && img) {
            usedImages.add(img);

            const discount = p.discountPercentage || 0;
            // Converting price to INR matching the design scale
            const price = Math.round(p.price * 80);
            const oldPrice = Math.round(price / (1 - discount / 100));

            mergedList.push({
              id: `dummy-${p.id}`,
              name: p.title,
              brand: p.brand || "NexaStore",
              category: normalizeCategory(p.category),
              price: price,
              oldPrice: oldPrice,
              rating: p.rating,
              stock: p.stock,
              description: p.description,
              image: img,
              badge: discount > 5 ? `${Math.round(discount)}% OFF` : "Sale",
              discount: discount,
              featured: p.rating > 4.5,
              salesCount: Math.round((p.stock || 10) * p.rating),
              createdDate: new Date(Date.now() - p.id * 24 * 60 * 60 * 1000),
            });
          }
        }

        // Normalize FakeStore products
        for (const p of fakeList) {
          const img = p.image;
          if (!usedImages.has(img) && img) {
            usedImages.add(img);

            const discount = Math.floor(Math.random() * 20) + 10; // 10-30%
            const price = Math.round(p.price * 80); // Converting price scale
            const oldPrice = Math.round(price / (1 - discount / 100));
            const ratingVal = p.rating ? p.rating.rate : 4.0;
            const stockVal = p.rating ? p.rating.count : 50;

            const titleWords = p.title.split(/[ \-_]/);
            const brand =
              titleWords.length > 0 && titleWords[0].length > 2
                ? titleWords[0]
                : "NexaStore";

            mergedList.push({
              id: `fake-${p.id}`,
              name: p.title,
              brand: brand,
              category: normalizeCategory(p.category),
              price: price,
              oldPrice: oldPrice,
              rating: ratingVal,
              stock: stockVal,
              description: p.description,
              image: img,
              badge: `${discount}% OFF`,
              discount: discount,
              featured: ratingVal > 4.3,
              salesCount: Math.round(stockVal * ratingVal),
              createdDate: new Date(
                Date.now() - (100 + p.id) * 24 * 60 * 60 * 1000
              ),
            });
          }
        }

        setProducts(mergedList);
        setCachedData(mergedList);
      } catch (err) {
        console.error("Dynamic product API loading failed:", err);
      } finally {
        setLoading(false);
      }
    };

    getProducts();
  }, []);

  // --- Dynamic Categories and Brands ---
  const dynamicCategories = [
    "All",
    ...new Set(products.map((p) => p.category)),
  ];
  const uniqueBrands = [...new Set(products.map((p) => p.brand))].sort();

  // --- Scroll Listener for Infinite Scroll ---
  useEffect(() => {
    const handleScroll = () => {
      if (
        window.innerHeight + window.scrollY >=
        document.documentElement.scrollHeight - 100
      ) {
        setVisibleCount((prev) => prev + 20);
      }
    };
    window.addEventListener("scroll", handleScroll);
    return () => window.removeEventListener("scroll", handleScroll);
  }, []);

  // Reset pagination when search terms or filter attributes change
  useEffect(() => {
    setVisibleCount(20);
  }, [
    search,
    category,
    selectedBrands,
    minPrice,
    maxPrice,
    minRating,
    onlyInStock,
    minDiscount,
    sortBy,
  ]);

  // --- Cart and Wishlist handlers ---
  const addWishlist = (product) => {
    const exist = wishlist.find((x) => x.id === product.id);
    if (!exist) {
      setWishlist([...wishlist, product]);
    }
  };

  const addCart = (product) => {
    const exist = cart.find((x) => x.id === product.id);
    if (!exist) {
      setCart([...cart, product]);
    }
  };

  const resetFilters = () => {
    setCategory("All");
    setSelectedBrands([]);
    setMinPrice("");
    setMaxPrice("");
    setMinRating(0);
    setOnlyInStock(false);
    setMinDiscount(0);
    setSortBy("Featured");
    setSearch("");
  };

  // --- Filtering & Sorting Operations ---
  const filteredProducts = products.filter((p) => {
    // Category match
    if (category !== "All" && p.category !== category) return false;

    // Brands match
    if (selectedBrands.length > 0 && !selectedBrands.includes(p.brand))
      return false;

    // Price range match
    if (minPrice !== "" && p.price < parseFloat(minPrice)) return false;
    if (maxPrice !== "" && p.price > parseFloat(maxPrice)) return false;

    // Rating match
    if (minRating > 0 && p.rating < minRating) return false;

    // Stock availability match
    if (onlyInStock && p.stock <= 0) return false;

    // Minimum discount match
    if (minDiscount > 0 && p.discount < minDiscount) return false;

    return true;
  });

  // Apply parsed query filtering
  const searchedProducts = parseSearchQuery(search, filteredProducts);

  // Apply sorting operations
  const sortedProducts = [...searchedProducts].sort((a, b) => {
    if (sortBy === "Price Low → High") {
      return a.price - b.price;
    }
    if (sortBy === "Price High → Low") {
      return b.price - a.price;
    }
    if (sortBy === "Highest Rated") {
      return b.rating - a.rating;
    }
    if (sortBy === "Best Selling") {
      return b.salesCount - a.salesCount;
    }
    if (sortBy === "Newest") {
      return b.createdDate - a.createdDate;
    }
    if (sortBy === "A-Z") {
      return a.name.localeCompare(b.name);
    }
    if (sortBy === "Z-A") {
      return b.name.localeCompare(a.name);
    }
    return b.featured - a.featured || b.rating - a.rating; // Default "Featured" sort
  });

  const visibleProducts = sortedProducts.slice(0, visibleCount);
  const hasMore = visibleCount < sortedProducts.length;

  return (
    <div className={darkMode ? "dark app" : "app"}>
      {/* Navbar */}
      <nav className="navbar">
        <div className="logo">
          🛒 <span>NexaStore</span>
        </div>

        <input
          type="text"
          placeholder="Search for products, brands and more..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />

        <div className="nav-buttons">
          <button>❤️ Wishlist ({wishlist.length})</button>
          <button>🛒 Cart ({cart.length})</button>
          <button onClick={() => setDarkMode(!darkMode)} className="dark-btn">
            {darkMode ? "☀ Light" : "🌙 Dark"}
          </button>
        </div>
      </nav>

      {/* Hero Banner */}
      <div className="hero">
        <div>
          <h1>Big Billion Days Sale</h1>
          <p>Up to 80% OFF on Mobiles, Laptops & Fashion</p>
          <button>Shop Now</button>
        </div>
      </div>

      {/* Dynamic Category Bar */}
      <div className="category-bar">
        {dynamicCategories.map((item) => (
          <button
            key={item}
            className={category === item ? "active" : ""}
            onClick={() => setCategory(item)}
          >
            {item}
          </button>
        ))}
      </div>

      {/* Mobile Filter Toggle Button */}
      <button
        className="mobile-filter-toggle"
        onClick={() => setMobileFilterOpen(!mobileFilterOpen)}
      >
        🔍 {mobileFilterOpen ? "Hide Filters" : "Show Filters & Sorting"}
      </button>

      {/* Main Layout Container */}
      <div className="main-layout">
        {/* Sidebar Filters */}
        <aside className={`sidebar ${mobileFilterOpen ? "show-mobile" : ""}`}>
          <div className="sidebar-header">
            <h2>Filters</h2>
            {(selectedBrands.length > 0 ||
              minPrice !== "" ||
              maxPrice !== "" ||
              minRating > 0 ||
              onlyInStock ||
              minDiscount > 0 ||
              category !== "All" ||
              search !== "") && (
              <button className="clear-btn" onClick={resetFilters}>
                Clear All
              </button>
            )}
          </div>

          {/* Sorting */}
          <div className="filter-section">
            <h3>Sort By</h3>
            <select
              className="filter-select"
              value={sortBy}
              onChange={(e) => setSortBy(e.target.value)}
            >
              <option value="Featured">Featured</option>
              <option value="Price Low → High">Price Low → High</option>
              <option value="Price High → Low">Price High → Low</option>
              <option value="Highest Rated">Highest Rated</option>
              <option value="Best Selling">Best Selling</option>
              <option value="Newest">Newest</option>
              <option value="A-Z">A-Z</option>
              <option value="Z-A">Z-A</option>
            </select>
          </div>

          {/* Brands Checklist */}
          <div className="filter-section">
            <h3>Brands</h3>
            <div className="brand-list">
              {uniqueBrands.map((brand) => (
                <label key={brand} className="brand-item">
                  <input
                    type="checkbox"
                    checked={selectedBrands.includes(brand)}
                    onChange={(e) => {
                      if (e.target.checked) {
                        setSelectedBrands([...selectedBrands, brand]);
                      } else {
                        setSelectedBrands(
                          selectedBrands.filter((b) => b !== brand)
                        );
                      }
                    }}
                  />
                  {brand}
                </label>
              ))}
            </div>
          </div>

          {/* Price Range Slider Inputs */}
          <div className="filter-section">
            <h3>Price Range (₹)</h3>
            <div className="range-inputs">
              <input
                type="number"
                placeholder="Min"
                value={minPrice}
                onChange={(e) => setMinPrice(e.target.value)}
              />
              <span className="range-separator">-</span>
              <input
                type="number"
                placeholder="Max"
                value={maxPrice}
                onChange={(e) => setMaxPrice(e.target.value)}
              />
            </div>
          </div>

          {/* Customer Ratings */}
          <div className="filter-section">
            <h3>Customer Rating</h3>
            {[4, 3, 2].map((stars) => (
              <button
                key={stars}
                className={`rating-filter-btn ${
                  minRating === stars ? "active" : ""
                }`}
                onClick={() => setMinRating(minRating === stars ? 0 : stars)}
              >
                ⭐ {stars}★ & above
              </button>
            ))}
          </div>

          {/* Min Discount Options */}
          <div className="filter-section">
            <h3>Discount</h3>
            <select
              className="filter-select"
              value={minDiscount}
              onChange={(e) => setMinDiscount(parseInt(e.target.value))}
            >
              <option value="0">All Discounts</option>
              <option value="10">10% Off or more</option>
              <option value="20">20% Off or more</option>
              <option value="30">30% Off or more</option>
              <option value="40">40% Off or more</option>
            </select>
          </div>

          {/* Stock Checkbox */}
          <div className="filter-section">
            <label className="checkbox-label">
              <input
                type="checkbox"
                checked={onlyInStock}
                onChange={(e) => setOnlyInStock(e.target.checked)}
              />
              <span>Exclude Out of Stock</span>
            </label>
          </div>
        </aside>

        {/* Content Area */}
        <div className="content-area">
          {/* Search result metrics bar */}
          {!loading && (
            <div className="search-status-bar">
              <p>
                Showing {sortedProducts.length} product
                {sortedProducts.length !== 1 ? "s" : ""}
                {search && ` for "${search}"`}
              </p>
            </div>
          )}

          {/* Product Cards and Grid */}
          <div className="products">
            {loading ? (
              Array.from({ length: 8 }).map((_, i) => (
                <div className="skeleton-card" key={i}>
                  <div className="skeleton-img"></div>
                  <div className="skeleton-text"></div>
                  <div className="skeleton-price"></div>
                  <div className="skeleton-btn"></div>
                </div>
              ))
            ) : visibleProducts.length > 0 ? (
              visibleProducts.map((item) => (
                <div className="card" key={item.id}>
                  <span className="badge">{item.badge}</span>
                  <img src={item.image} alt={item.name} />
                  <h3>{item.name}</h3>
                  <div className="rating">⭐ {item.rating}</div>
                  <h2>₹{item.price}</h2>
                  <del>₹{item.oldPrice}</del>
                  <p className="discount">
                    {Math.round(
                      ((item.oldPrice - item.price) / item.oldPrice) * 100
                    )}
                    % OFF
                  </p>
                  <div className="btns">
                    <button className="wish" onClick={() => addWishlist(item)}>
                      ❤️
                    </button>
                    <button className="cart-btn" onClick={() => addCart(item)}>
                      🛒 Add To Cart
                    </button>
                  </div>
                </div>
              ))
            ) : (
              <div className="no-products-found">
                <h3>No products found</h3>
                <p>Try refining your search terms or clearing your filters.</p>
                <button className="cart-btn" style={{ width: "auto", padding: "10px 20px" }} onClick={resetFilters}>
                  Reset All Filters
                </button>
              </div>
            )}
          </div>

          {/* Infinite scroll load indicator */}
          {!loading && hasMore && (
            <div className="infinite-loader">Scroll down to load more products...</div>
          )}
        </div>
      </div>
    </div>
  );
}

export default App;