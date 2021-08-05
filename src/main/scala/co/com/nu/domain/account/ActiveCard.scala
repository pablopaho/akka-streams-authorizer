package co.com.nu.domain.account

sealed trait ActiveCard

object ActiveCard {

  def apply(status: Boolean): ActiveCard = {
    status match {
      case true => Active
      case false => Inactive
    }
  }

  def transformToBoolean(activeCard: ActiveCard): Boolean =
    activeCard match {
      case Active => true
      case Inactive => false
    }

  case object Active extends ActiveCard
  case object Inactive extends ActiveCard
}
