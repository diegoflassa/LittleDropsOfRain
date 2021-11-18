package app.web.diegoflassa_site.littledropsofrain.data.helpers

import app.web.diegoflassa_site.littledropsofrain.data.entities.CategoryItem
import app.web.diegoflassa_site.littledropsofrain.data.entities.Product
import app.web.diegoflassa_site.littledropsofrain.data.entities.Source

class DummyData {
    companion object{
        fun getNewCollectionCarouselItems() : List<Product>{
            val ret= mutableListOf<Product>()
            ret.add(Product("", "0", "0", "", "Test A", 0,true,"true","","","",mutableListOf("11"), Source.ILURIA.name, mutableListOf("Categoria A", "Categoria B")))
            ret.add(Product("", "0", "0", "", "Test B", 0,true,"true","","","",mutableListOf("11"), Source.ILURIA.name, mutableListOf("Categoria C", "Categoria D")))
            ret.add(Product("", "0", "0", "", "Test C", 0,true,"true","","","",mutableListOf("11"), Source.ILURIA.name, mutableListOf("Categoria E", "Categoria F")))
            return ret
        }

        fun getRecommendationsCarouselItems() : List<Product>{
            val ret= mutableListOf<Product>()
            ret.add(Product("", "0", "0", "", "Test A", 0,true,"true","","","",mutableListOf("11"), Source.ILURIA.name, mutableListOf("Categoria A", "Categoria B")))
            ret.add(Product("", "0", "0", "", "Test B", 0,true,"true","","","",mutableListOf("11"), Source.ILURIA.name, mutableListOf("Categoria C", "Categoria D")))
            ret.add(Product("", "0", "0", "", "Test C", 0,true,"true","","","",mutableListOf("11"), Source.ILURIA.name, mutableListOf("Categoria E", "Categoria F")))
            return ret
        }

        fun getSpotlightCarouselItems() : List<Product>{
            val ret= mutableListOf<Product>()
            ret.add(Product("", "0", "0", "", "Test A", 0,true,"true","","","",mutableListOf("11"), Source.ILURIA.name, mutableListOf("Categoria A", "Categoria B")))
            ret.add(Product("", "0", "0", "", "Test B", 0,true,"true","","","",mutableListOf("11"), Source.ILURIA.name, mutableListOf("Categoria C", "Categoria D")))
            ret.add(Product("", "0", "0", "", "Test C", 0,true,"true","","","",mutableListOf("11"), Source.ILURIA.name, mutableListOf("Categoria E", "Categoria F")))
            return ret
        }

        fun getCategoriesCarouselItems() : List<CategoryItem>{
            val ret= mutableListOf<CategoryItem>()
            ret.add(CategoryItem(null, "Category A"))
            ret.add(CategoryItem(null, "Category B"))
            ret.add(CategoryItem(null, "Category C"))
            return ret
        }
    }
}