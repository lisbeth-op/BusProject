using System;
using DadoTramposo;

namespace DadoTramposo
{
    class Program
    {
        static void Main(string[] args)
        {
            var dado = new Dado();
            int opcionMenu;

            do
            {
                ImprimirMenu();
                opcionMenu = Utils.ReadIntInput("opción");

                switch (opcionMenu)
                {
                    case 1: 
                        LanzarDado(dado);
                        break;
                    case 2: 
                        CambiarProbabilidades(dado);
                        break;
                    case 3: 
                        dado.MostrarEstadisticas();
                        break;
                    case 4: 
                        dado.ReiniciarEstadisticas();
                        Console.WriteLine("Estadísticas reiniciadas.");
                        break;
                    case 5: 
                        Console.WriteLine("Saliendo del programa.");
                        break;
                    default:
                        Console.WriteLine("Opción inválida. Por favor elija una opción válida del menú.");
                        break;
                }
                Pause();
            } while (opcionMenu != 5);
        }

        static void LanzarDado(Dado dado)
        {
            Console.Clear();
            Console.Write("Digite la cantidad de veces que desea lanzar el dado: ");
            var veces = Utils.ReadIntInput("veces");
            dado.SimularMuchosLanzamientos(veces);
        }

        static void CambiarProbabilidades(Dado dado)
        {
            Console.Clear();
            Console.Write("Digite el número del lado a modificar (1 - 6): ");
            var lado = Utils.ReadIntInput("número");

            while (lado < 1 || lado > 6)
            {
                Console.Write("Número inválido. Digite un número entre 1 y 6: ");
                lado = Utils.ReadIntInput("número");
            }

            Console.Write($"Digite la nueva probabilidad para el lado {lado} (0-100): ");
            var probabilidad = Utils.ReadIntInput("probabilidad");

            while (probabilidad < 0 || probabilidad > 100)
            {
                Console.Write("Probabilidad inválida. Digite un número entre 0 y 100: ");
                probabilidad = Utils.ReadIntInput("probabilidad");
            }

            dado.CambiarProbabilidad(lado, probabilidad);
            Console.WriteLine($"Probabilidad del lado {lado} actualizada a {probabilidad}%.");
        }

        static void ImprimirMenu()
        {
            Console.Clear();
           Console.WriteLine("  1- Simular                2- Cambiar probabilidades                3- Ver estadísticas        ");
            Console.WriteLine("  4- Reiniciar estadísticas        5- Salir                                                   ");
  ;
            //Console.Write("Digite la opción deseada: ");
        }

        static void Pause()
        {
            Console.WriteLine();
            Console.Write("Presione Enter para continuar...");
            Console.ReadKey();
        }
    }
}
